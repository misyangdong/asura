/**
 * @FileName: AbstractRabbitMqReceiver.java
 * @Package: com.asura.framework.rabbitmq.receive
 * @author sence
 * @created 3/9/2016 5:22 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.receive;

import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.exception.AsuraRabbitMqException;
import com.rabbitmq.client.Connection;

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
public abstract class AbstractRabbitMqReceiver implements IRabbitMqReceiver {

    /**
     * MQ factory
     */
    private RabbitConnectionFactory rabbitConnectionFactory;

    /**
     * 消息实际的监听器
     */
    private List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners;

    /**
     * 空构造方法
     */
    public AbstractRabbitMqReceiver() {
        this.rabbitConnectionFactory = new RabbitConnectionFactory();
        rabbitConnectionFactory.init();
    }

    public AbstractRabbitMqReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners) {
        this.rabbitConnectionFactory = rabbitConnectionFactory;
        this.rabbitMqMessageLiteners = rabbitMqMessageLiteners;
    }


    @Override
    public void receiveMessage() throws Exception {
        if (rabbitMqMessageLiteners == null || rabbitMqMessageLiteners.isEmpty()) {
            throw new AsuraRabbitMqException("rabbitMqMessageLiteners not init");
        }
        /**
         * http://stackoverflow.com/questions/18418936/rabbitmq-and-relationship-between-channel-and-connection
         * A Connection represents a real TCP connection to the message broker
         * where as a channel is a virtual connection inside it.
         * This way you can use as many (virtual) connections as you want inside your application without overloading the broker with TCP connections
         */
        Connection connection = rabbitConnectionFactory.getConnection();
        doConsumeMessage(connection, rabbitConnectionFactory.getEnvironment());
    }

    /**
     * 使用单个Connection 来实现消费
     *
     * @param connection
     */
    protected abstract void doConsumeMessage(Connection connection, String environment) throws IOException, InterruptedException;

    public void setRabbitConnectionFactory(RabbitConnectionFactory rabbitConnectionFactory) {
        this.rabbitConnectionFactory = rabbitConnectionFactory;
    }


    public List<IRabbitMqMessageLisenter> getRabbitMqMessageLiteners() {
        return rabbitMqMessageLiteners;
    }

    public void setRabbitMqMessageLiteners(List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners) {
        this.rabbitMqMessageLiteners = rabbitMqMessageLiteners;
    }

}
