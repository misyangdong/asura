/**
 * @FileName: T_RabbitMqReceiver.java
 * @Package: com.asura.framework.rabbbitmq
 * @author sence
 * @created 3/10/2016 5:32 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbbitmq;

import com.asura.framework.rabbitmq.PublishSubscribeType;
import com.asura.framework.rabbitmq.SampleMessageLitener;
import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.receive.IRabbitMqMessageLisenter;
import com.asura.framework.rabbitmq.receive.IRabbitMqReceiver;
import com.asura.framework.rabbitmq.receive.queue.ExcutorRabbitMqQueueReceiver;
import com.asura.framework.rabbitmq.receive.queue.RabbitMqQueueReceiver;
import com.asura.framework.rabbitmq.receive.topic.ExcutorRabbitMqTopicReceiver;
import com.asura.framework.rabbitmq.receive.topic.RabbitMqTopicReceiver;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
public class T_RabbitMqReceiver {


    @Test
    public void testSingleThradReceiver() throws Exception {
        RabbitMqQueueReceiver rabbitMqReceiver = new RabbitMqQueueReceiver();
        rabbitMqReceiver.setQueueName("LSQ_QUEUE_03");
        RabbitConnectionFactory rabbitConnectionFactory = new RabbitConnectionFactory();
        rabbitConnectionFactory.init();
        rabbitMqReceiver.setRabbitConnectionFactory(rabbitConnectionFactory);
        IRabbitMqMessageLisenter lisenter = new SampleMessageLitener();
        List<IRabbitMqMessageLisenter> lisenterList = new ArrayList<>();
        lisenterList.add(lisenter);
        rabbitMqReceiver.setRabbitMqMessageLiteners(lisenterList);
        rabbitMqReceiver.receiveMessage();
    }


    @Test
    public void testExecutorReceiver() throws Exception {
        ExcutorRabbitMqQueueReceiver rabbitMqReceiver = new ExcutorRabbitMqQueueReceiver();
        rabbitMqReceiver.setQueueName("LSQ_QUEUE_03");
        RabbitConnectionFactory rabbitConnectionFactory = new RabbitConnectionFactory();
        rabbitConnectionFactory.init();
        rabbitMqReceiver.setPoolSize(5);
        rabbitMqReceiver.setRabbitConnectionFactory(rabbitConnectionFactory);
        IRabbitMqMessageLisenter lisenter = new SampleMessageLitener();
        List<IRabbitMqMessageLisenter> lisenterList = new ArrayList<>();
        lisenterList.add(lisenter);
        rabbitMqReceiver.setRabbitMqMessageLiteners(lisenterList);
        rabbitMqReceiver.receiveMessage();
        Thread.sleep(1000000);
    }

    @Test
    public void testExecutorTopicReceiver() throws Exception {
        RabbitMqTopicReceiver rabbitMqReceiver = new RabbitMqTopicReceiver();
        RabbitConnectionFactory rabbitConnectionFactory = new RabbitConnectionFactory();
        rabbitConnectionFactory.init();
        rabbitMqReceiver.setRabbitConnectionFactory(rabbitConnectionFactory);
        IRabbitMqMessageLisenter lisenter = new SampleMessageLitener();
        List<IRabbitMqMessageLisenter> lisenterList = new ArrayList<>();
        lisenterList.add(lisenter);
        rabbitMqReceiver.setRabbitMqMessageLiteners(lisenterList);
        rabbitMqReceiver.setBindingKey("xxx");
        rabbitMqReceiver.setExchangeName("LSQ_EXCHANGE_D_01");
        rabbitMqReceiver.setPublishSubscribeType(PublishSubscribeType.DIRECT);
        rabbitMqReceiver.receiveMessage();
        Thread.sleep(1000000);
    }

}
