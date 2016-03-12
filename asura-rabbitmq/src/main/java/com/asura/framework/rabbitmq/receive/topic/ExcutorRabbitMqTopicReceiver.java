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
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, queueName,bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,String queueName,  String bindingKey, String exchangeName, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,queueName, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, String queueName, String bindingKey, String exchangeName, PublishSubscribeType publishSubscribeType, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,queueName, bindingKey, exchangeName, publishSubscribeType);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,  String bindingKey, String exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, null,bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, String bindingKey, String exchangeName, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,null, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, String bindingKey, String exchangeName, PublishSubscribeType publishSubscribeType, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,null, bindingKey, exchangeName, publishSubscribeType);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public void doConsumeTopicMessage(Connection connection) throws IOException, InterruptedException {
        for(int size = 0;size<this.getPoolSize();size++) {
            this.executorService.submit(new ConsumeWorker(connection, getExchangeName(),getQueueName() ,getBindingKey(), getPublishSubscribeType().getName(), getRabbitMqMessageLiteners()));
        }
    }

    private class ConsumeWorker implements Runnable {

        private String queueName;

        private Connection connection;

        private String exchangeName;

        private String bindingKey;

        private String routingType;

        private List<IRabbitMqMessageLisenter> lisenters;

        private ConsumeWorker(Connection connection, String exchangeName, String queueName,String bindingKey, String routingType, List<IRabbitMqMessageLisenter> lisenters) {
            this.bindingKey = bindingKey;
            this.exchangeName = exchangeName;
            this.routingType = routingType;
            this.connection = connection;
            this.lisenters = lisenters;
            this.queueName = queueName;
        }

        @Override
        public void run() {
            try {
                Channel channel = connection.createChannel();
                channel.exchangeDeclare(exchangeName, routingType, true);
                if(queueName ==null){
                    queueName = channel.queueDeclare().getQueue();
                }else{
                    channel.queueDeclare(queueName,true,false,false,null);
                }
                channel.queueBind(queueName, exchangeName, bindingKey);
                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicConsume(queueName,false,consumer);
                while(true){
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    for(IRabbitMqMessageLisenter lisenter:lisenters){
                        try {
                            lisenter.processMessage(delivery);
                        }catch (Exception e){
                            //TODO error message
                        }
                    }
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                }
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
