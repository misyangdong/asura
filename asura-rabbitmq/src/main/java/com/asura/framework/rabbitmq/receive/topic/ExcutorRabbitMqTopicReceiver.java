/**
 * @FileName: ExcutorRabbitMqTopicReceiver.java
 * @Package: com.asura.framework.rabbitmq.receive
 * @author sence
 * @created 3/9/2016 7:31 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.receive.topic;

import com.asura.framework.rabbitmq.PublishSubscribeType;
import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.receive.IRabbitMqMessageLisenter;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>多线程消费</p>
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
public class ExcutorRabbitMqTopicReceiver extends AbstractRabbitMqTopicReceiver {

    /**
     *
     */
    private ExecutorService executorService;

    private int poolSize;

    public ExcutorRabbitMqTopicReceiver() {
        super();
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, String queueName, String bindingKey, String exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, String bindingKey, String exchangeName, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, String bindingKey, String exchangeName, PublishSubscribeType publishSubscribeType, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, bindingKey, exchangeName, publishSubscribeType);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public void doConsumeTopicMessage(Connection connection) throws IOException, InterruptedException {
        for(int size = 0;size<this.getPoolSize();size++) {
            this.executorService.submit(new ConsumeWorker(connection, getExchangeName(), getBindingKey(), getPublishSubscribeType().getName(), getRabbitMqMessageLiteners()));
        }
    }

    private class ConsumeWorker implements Runnable {

        private Connection connection;

        private String exchangeName;

        private String bindingKey;

        private String routingType;

        private List<IRabbitMqMessageLisenter> lisenters;

        private ConsumeWorker(Connection connection, String exchangeName, String bindingKey, String routingType, List<IRabbitMqMessageLisenter> lisenters) {
            this.bindingKey = bindingKey;
            this.exchangeName = exchangeName;
            this.routingType = routingType;
            this.connection = connection;
            this.lisenters = lisenters;
        }

        @Override
        public void run() {
            try {
                Channel channel = connection.createChannel();
                channel.exchangeDeclare(exchangeName, routingType, true);
                String queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, exchangeName, bindingKey);
                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        String message = new String(body, "UTF-8");
                        System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                    }
                };
                channel.basicConsume(queueName, true, consumer);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
        executorService = Executors.newFixedThreadPool(poolSize);
    }
}
