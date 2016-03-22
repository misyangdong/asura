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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

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

    private final static Logger LOGGER = LoggerFactory.getLogger(ExcutorRabbitMqTopicReceiver.class);
    /**
     *
     */
    private ExecutorService executorService;

    private int poolSize;

    public ExcutorRabbitMqTopicReceiver() {
        super();
        poolSize = 3;
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, queueName,bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        poolSize = 3;
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,queueName, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        this.poolSize = poolSize;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName, PublishSubscribeType publishSubscribeType, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,queueName, bindingKey, exchangeName, publishSubscribeType);
        this.poolSize = poolSize;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        BindingKey bindingKey, ExchangeName exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners, null,bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        poolSize = 3;
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        BindingKey bindingKey, ExchangeName exchangeName, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,null, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
        this.poolSize = poolSize;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public ExcutorRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                        BindingKey bindingKey, ExchangeName exchangeName, PublishSubscribeType publishSubscribeType, int poolSize) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,null, bindingKey, exchangeName, publishSubscribeType);
        this.poolSize = poolSize;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public void doConsumeTopicMessage(Connection connection,String environment) throws IOException, InterruptedException {
        for(int size = 0;size<this.getPoolSize();size++) {
            this.executorService.submit(new ConsumeWorker(connection,environment, getExchangeName(),getQueueName() ,getBindingKey(), getPublishSubscribeType().getName(), getRabbitMqMessageLiteners()));
        }
    }

    private class ConsumeWorker implements Runnable {

        private QueueName queueName;

        private Connection connection;

        private ExchangeName exchangeName;

        private BindingKey bindingKey;

        private String routingType;

        private String environment;

        private List<IRabbitMqMessageLisenter> lisenters;

        private ConsumeWorker(Connection connection,String environment, ExchangeName exchangeName, QueueName queueName,BindingKey bindingKey, String routingType, List<IRabbitMqMessageLisenter> lisenters) {
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
                if(queueName == null){
                    qname = channel.queueDeclare().getQueue();
                }else{
                    qname = queueName.getNameByEnvironment(environment);
                    channel.queueDeclare(qname,true,false,false,null);
                }
                channel.queueBind(qname, _exchangeName, bindingKey.getKey());
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
                if(e instanceof ShutdownSignalException){
                    try {
                        channel.close();
                        connection.close();
                    } catch (IOException e1) {
                        LOGGER.error("rabbmitmq close error:", e);
                    } catch (TimeoutException e1) {
                        LOGGER.error("rabbmitmq close error:", e);
                    }
                }
                LOGGER.error("rabbmitmq consumer error:", e);
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
