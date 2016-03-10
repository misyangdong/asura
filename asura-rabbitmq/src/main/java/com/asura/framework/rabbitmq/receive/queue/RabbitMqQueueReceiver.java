/**
 * @FileName: SampleRabbitMqReceiver.java
 * @Package: com.asura.framework.rabbitmq.receive
 * @author sence
 * @created 3/9/2016 6:23 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.receive.queue;

import com.asura.framework.base.exception.BusinessException;
import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.receive.AbstractRabbitMqReceiver;
import com.asura.framework.rabbitmq.receive.IRabbitMqMessageLisenter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.List;

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


    /**
     *
     */
    public RabbitMqQueueReceiver(){

    }

    /**
     *
     * @param rabbitConnectionFactory
     * @param rabbitMqMessageLiteners
     * @param queueName
     */
    public RabbitMqQueueReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,String queueName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,queueName);
    }


    /**
     *
     * @param connection
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void doConsumeQueueMessage(Connection connection) throws IOException, InterruptedException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(this.getQueueName(), true, false, false, null);
        channel.basicQos(1);
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(this.getQueueName(),false,consumer);
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            for(IRabbitMqMessageLisenter lisenter:super.getRabbitMqMessageLiteners()){
                lisenter.processMessage(delivery);
            }
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        }
    }

}
