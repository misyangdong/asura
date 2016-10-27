/**
 * @FileName: ExcutorRabbitMqQueueRceiver.java
 * @Package: com.asura.framework.rabbitmq.receive
 * @author sence
 * @created 3/9/2016 7:15 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.receive.queue;

import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.entity.QueueName;
import com.asura.framework.rabbitmq.exception.AsuraRabbitMqException;
import com.asura.framework.rabbitmq.receive.IRabbitMqMessageLisenter;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * <p></p>
 * <p/>
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author sence
 * @version 1.0
 * @since 1.0
 */
public class ExcutorRabbitMqQueueReceiver extends AbstractRabbitQueueReceiver {


    private final static Logger LOGGER = LoggerFactory.getLogger(ExcutorRabbitMqQueueReceiver.class);

    private ExecutorService executorService;

    private int poolSize;

    public ExcutorRabbitMqQueueReceiver() {
        super();
        poolSize = 3;
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqQueueReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, QueueName queueName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, queueName);
        poolSize = 3;
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqQueueReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, int poolSize, QueueName queueName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, queueName);
        this.poolSize = poolSize;
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    protected void doConsumeQueueMessage(Connection connection, String environment) {
        for (int i = 0; i < poolSize; i++) {
            this.executorService.submit(new ConsumeWorker(this.getQueueName(), connection, environment, this.getRabbitMqMessageLiteners()));
        }
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    private class ConsumeWorker implements Runnable {

        private QueueName queueName;

        private Connection connection;

        private List<IRabbitMqMessageLisenter> lisenters;

        private String enviroment;

        private ConsumeWorker(QueueName queueName, Connection connection, String environment, List<IRabbitMqMessageLisenter> lisenters) {
            this.queueName = queueName;
            this.connection = connection;
            this.lisenters = lisenters;
            this.enviroment = environment;
        }

        @Override
        public void run() {
            Channel channel = null;

            try {
                if (queueName == null) {
                    throw new AsuraRabbitMqException("queueName not set");
                }
                channel = connection.createChannel();
                String _queueName = queueName.getNameByEnvironment(enviroment);
                channel.queueDeclare(_queueName, true, false, false, null);
                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicQos(1);
                channel.basicConsume(_queueName, false, consumer);
                while (true) {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    Transaction trans = Cat.newTransaction("RabbitMQ Message", "CONSUME-QUEUE-" + _queueName);
                    String message = new String(delivery.getBody(), "UTF-8");
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("CONSUMER QUEUE MESSAGE:[queue:{},message:{}]", _queueName, message);
                    }
                    Cat.logEvent("queue name", _queueName);
                    Cat.logEvent("queue message", message);
                    Cat.logMetricForCount("CONSUME-QUEUE-" + _queueName); // 统计请求次数, 可以查看对应队列中放入了多少信息
                    try {
                        for (IRabbitMqMessageLisenter lisenter : lisenters) {
                            lisenter.processMessage(delivery);
                        }
                        trans.setStatus(Transaction.SUCCESS);
                    } catch (Exception e) {
                        Cat.logError("队列[" + _queueName + "]消费异常", e);
                        trans.setStatus(e);
                    } finally {
                        trans.complete();
                    }
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            } catch (Exception e) {
                if (e instanceof ShutdownSignalException) {
                    try {
                        channel.close();
                        connection.close();
                    } catch (IOException e1) {
                        if (LOGGER.isErrorEnabled()) {
                            LOGGER.error("rabbmitmq close error:", e);
                        }
                    } catch (TimeoutException e1) {
                        if (LOGGER.isErrorEnabled()) {
                            LOGGER.error("rabbmitmq close error:", e);
                        }
                    }
                }
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("rabbmitmq consumer error:", e);
                }
            }

        }
    }
}