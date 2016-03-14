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
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.List;

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


    public RabbitMqTopicReceiver() {

    }

    public RabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                 BindingKey bindingKey, ExchangeName exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,null, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
    }

    public RabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                 BindingKey bindingKey, ExchangeName exchangeName, PublishSubscribeType publishSubscribeType) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,null, bindingKey, exchangeName, publishSubscribeType);
    }

    public RabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                 QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,queueName, bindingKey, exchangeName, PublishSubscribeType.DIRECT);
    }

    public RabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners,
                                 QueueName queueName, BindingKey bindingKey, ExchangeName exchangeName, PublishSubscribeType publishSubscribeType) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners,queueName, bindingKey, exchangeName, publishSubscribeType);
    }

    /**
     * 执行真正的消费工作
     * @param connection
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void doConsumeTopicMessage(Connection connection) throws IOException, InterruptedException {
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(this.getExchangeName().getName(), this.getPublishSubscribeType().getName(), true);
        String qName = null;
        if(this.getQueueName() ==null){
            qName = channel.queueDeclare().getQueue();
        }else{
            qName = this.getQueueName().getName();
            channel.queueDeclare(qName ,true,false,false,null);
        }
        channel.queueBind(qName, this.getExchangeName().getName(), this.getBindingKey().getKey());
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(qName ,false,consumer);
        while(true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            for(IRabbitMqMessageLisenter lisenter:super.getRabbitMqMessageLiteners()){
                lisenter.processMessage(delivery);
                //TODO
            }
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        }
    }

}
