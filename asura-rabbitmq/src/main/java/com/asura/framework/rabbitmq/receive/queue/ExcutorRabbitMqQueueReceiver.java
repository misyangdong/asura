/**
 * @FileName: ExcutorRabbitMqQueueRceiver.java
 * @Package: com.asura.framework.rabbitmq.receive
 * @author sence
 * @created 3/9/2016 7:15 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.receive.queue;

import com.asura.framework.base.exception.BusinessException;
import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.entity.QueueName;
import com.asura.framework.rabbitmq.receive.AbstractRabbitMqReceiver;
import com.asura.framework.rabbitmq.receive.IRabbitMqMessageLisenter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p></p>
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author sence
 * @since 1.0
 * @version 1.0
 */
public class ExcutorRabbitMqQueueReceiver extends AbstractRabbitQueueReceiver {


    private ExecutorService executorService;

    private int poolSize;

    public ExcutorRabbitMqQueueReceiver(){
        super();
        poolSize = 3;
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqQueueReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,QueueName queueName) {
        super(rabbitConnectionFactory,rabbitMqMessageLiteners,queueName);
        poolSize = 3;
        executorService = Executors.newFixedThreadPool(3);
    }

    public ExcutorRabbitMqQueueReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, int poolSize, QueueName queueName) {
        super(rabbitConnectionFactory,rabbitMqMessageLiteners,queueName);
        this.poolSize = poolSize;
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    protected void doConsumeQueueMessage(Connection connection){
        for(int i = 0; i<poolSize;i++) {
            this.executorService.submit(new ConsumeWorker(this.getQueueName(), connection, this.getRabbitMqMessageLiteners()));
        }
    }

    private class ConsumeWorker implements Runnable{

        private QueueName queueName;

        private Connection connection;

        private List<IRabbitMqMessageLisenter> lisenters;

        private ConsumeWorker(QueueName queueName,Connection connection,List<IRabbitMqMessageLisenter> lisenters){
            this.queueName = queueName;
            this.connection = connection;
            this.lisenters = lisenters;
        }

        @Override
        public void run() {
            try {
                if(queueName == null){
                    throw new BusinessException("queueName not set");
                }
                Channel channel = connection.createChannel();
                channel.queueDeclare(queueName.getName(), true, false, false, null);
                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicQos(1);
                channel.basicConsume(queueName.getName(),false,consumer);
                while(true){
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    for(IRabbitMqMessageLisenter lisenter:lisenters){
                        lisenter.processMessage(delivery);
                    }
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                }
            }catch (Exception e){
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
