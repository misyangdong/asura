/**
 * @FileName: RabbitMqTopicReceiver.java
 * @Package: com.asura.framework.rabbitmq.receive
 * @author sence
 * @created 3/9/2016 6:32 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.receive.topic;

import com.asura.framework.rabbitmq.PublishSubscribeType;
import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.entity.BindingKey;
import com.asura.framework.rabbitmq.entity.ExchangeName;
import com.asura.framework.rabbitmq.entity.QueueName;
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
public class RabbitMqTopicReceiver extends AbstractRabbitMqTopicReceiver {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitMqTopicReceiver.class);

    public RabbitMqTopicReceiver() {

    }

    public RabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                 BindingKey bindingKey, ExchangeName exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, null, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
    }

    public RabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                 BindingKey bindingKey, ExchangeName exchangeName, PublishSubscribeType publishSubscribeType) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, null, bindingKey, exchangeName, publishSubscribeType);
    }

    public RabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                 QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, queueName, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
    }

    public RabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                 QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName, PublishSubscribeType publishSubscribeType) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, queueName, bindingKey, exchangeName, publishSubscribeType);
    }

    /**
     * 执行真正的消费工作
     *
     * @param connection
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void doConsumeTopicMessage(Connection connection, String environment) throws IOException, InterruptedException {
        ConsumeWorker consumeWorker = new ConsumeWorker(connection, environment, getExchangeName(), getQueueName(), getBindingKey(), getPublishSubscribeType().getName(), getRabbitMqMessageLiteners());
        new Thread(consumeWorker).start();
    }

    private class ConsumeWorker implements Runnable {

        private QueueName queueName;

        private Connection connection;

        private ExchangeName exchangeName;

        private BindingKey bindingKey;

        private String routingType;

        private String environment;

        private List<IRabbitMqMessageLisenter> lisenters;

        private ConsumeWorker(Connection connection, String environment, ExchangeName exchangeName, QueueName queueName, BindingKey bindingKey, String routingType, List<IRabbitMqMessageLisenter> lisenters) {
            this.bindingKey = bindingKey;
            this.exchangeName = exchangeName;
            this.routingType = routingType;
            this.connection = connection;
            this.lisenters = lisenters;
            this.queueName = queueName;
            this.environment = environment;
        }

        @Override
        public void run() {
            Channel channel = null;
            try {
                channel = connection.createChannel();
                String _exchangeName = exchangeName.getNameByEnvironment(environment);
                channel.exchangeDeclare(_exchangeName, routingType, true);
                String qname;
                if (queueName == null) {
                    qname = channel.queueDeclare().getQueue();
                } else {
                    qname = queueName.getNameByEnvironment(environment);
                    channel.queueDeclare(qname, true, false, false, null);
                }
                channel.queueBind(qname, _exchangeName, bindingKey.getKey());
                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicConsume(qname, false, consumer);
                while (true) {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    Transaction trans = Cat.newTransaction("RabbitMQ Message", "CONSUME-TOPIC-" + _exchangeName);
                    String message = new String(delivery.getBody(), "UTF-8");
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("CONSUMER TOPIC MESSAGE:[exchange:{},queue:{},bindingKey:{},message:{}]", _exchangeName, qname, bindingKey.getKey(), message);
                    }
                    Cat.logEvent("exchange name", _exchangeName);
                    Cat.logEvent("queue name", qname);
                    Cat.logEvent("bind key", bindingKey.getKey());
                    Cat.logEvent("message", message);
                    Cat.logMetricForCount("CONSUME-TOPIC-" + _exchangeName);
                    try {
                        for (IRabbitMqMessageLisenter lisenter : lisenters) {
                            lisenter.processMessage(delivery);
                        }
                        trans.setStatus(Transaction.SUCCESS);
                    } catch (Exception e) {
                        Cat.logError("队列[" + _exchangeName + "," + qname + "," + bindingKey.getKey() + "]消费异常", e);
                        LOGGER.error("CONSUMER TOPIC MESSAGE:[exchange:{},queue:{},bindingKey:{},message:{}]", _exchangeName, qname, bindingKey.getKey(), message);
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
