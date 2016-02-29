package com.asure.framework.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class QueueOneLitener implements  MessageListener{
    @Override
    public void onMessage(Message message) {
        System.out.println(" data :" + message.getBody());
    }
}