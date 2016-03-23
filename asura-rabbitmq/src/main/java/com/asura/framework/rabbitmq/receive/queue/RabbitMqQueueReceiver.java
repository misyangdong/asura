/**
 * @FileName: SampleRabbitMqReceiver.java
 * @Package: com.asura.framework.rabbitmq.receive
 * @author sence
 * @created 3/9/2016 6:23 PM
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
import java.util.concurrent.TimeoutException;

/**
 * <p>队列消息接受</p>
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
public class RabbitMqQueueReceiver extends AbstractRabbitQueueReceiver {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitMqQueueReceiver.class);

    /**
     *
     */
    public RabbitMqQueueReceiver() {

    }

    /**
     * @param rabbitConnectionFactory
     * @param rabbitMqMessageLiteners
     * @param queueName
     */
    public RabbitMqQueueReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, QueueName queueName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, queueName);
    }


    /**
     * @param connection
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void doConsumeQueueMessage(Connection connection, String environment) throws IOException, InterruptedException {
        new Thread(new ConsumeWorker(this.getQueueName(), connection, environment, this.getRabbitMqMessageLiteners())).start();

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
            Transaction trans = Cat.newTransaction("RabbitMQ Message", "consume queue");
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
                    if(LOGGER.isInfoEnabled()) {
                        LOGGER.info("CONSUMER TOPIC MESSAGE:[queue:{},message:{}]", _queueName, new String(delivery.getBody(), "UTF-8"));
                    }

                    for (IRabbitMqMessageLisenter lisenter : lisenters) {
                        lisenter.processMessage(delivery);
                    }
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            } catch (Exception e) {
                if(e instanceof ShutdownSignalException){
                    try {
                        channel.close();
                        connection.close();
                    } catch (IOException e1) {
                        if(LOGGER.isErrorEnabled()) {
                            LOGGER.error("rabbmitmq close error:", e);
                        }
                    } catch (TimeoutException e1) {
                        if(LOGGER.isErrorEnabled()) {
                            LOGGER.error("rabbmitmq close error:", e);
                        }
                    }
                }
                if(LOGGER.isErrorEnabled()) {
                    LOGGER.error("rabbmitmq consumer error:", e);
                }
            }

        }
    }

}
