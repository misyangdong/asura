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
import com.asura.framework.rabbitmq.entity.BindingKey;
import com.asura.framework.rabbitmq.entity.ExchangeName;
import com.asura.framework.rabbitmq.entity.QueueName;
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

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, queueName,bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,queueName, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName, PublishSubscribeType publishSubscribeType, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,queueName, bindingKey, exchangeName, publishSubscribeType);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        BindingKey bindingKey, ExchangeName exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, null,bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        BindingKey bindingKey, ExchangeName exchangeName, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,null, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        BindingKey bindingKey, ExchangeName exchangeName, PublishSubscribeType publishSubscribeType, int poolSize) {
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

        private QueueName queueName;

        private Connection connection;

        private ExchangeName exchangeName;

        private BindingKey bindingKey;

        private String routingType;

        private List<IRabbitMqMessageLisenter> lisenters;

        private ConsumeWorker(Connection connection, ExchangeName exchangeName, QueueName queueName,BindingKey bindingKey, String routingType, List<IRabbitMqMessageLisenter> lisenters) {
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
                channel.exchangeDeclare(exchangeName.getName(), routingType, true);
                String qname = null;
                if(queueName == null){
                    qname = channel.queueDeclare().getQueue();
                }else{
                    qname = queueName.getName();
                    channel.queueDeclare(qname,true,false,false,null);
                }
                channel.queueBind(qname, exchangeName.getName(), bindingKey.getKey());
                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicConsume(qname, false, consumer);
                while(true){
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    for(IRabbitMqMessageLisenter lisenter:lisenters){
                            lisenter.processMessage(delivery);
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
