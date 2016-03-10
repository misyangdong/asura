/**
 * @FileName: AbstractRabbitQueueReceiver.java
 * @Package: com.asura.framework.rabbitmq.receive
 * @author sence
 * @created 3/9/2016 8:52 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.receive.queue;

import com.asura.framework.base.exception.BusinessException;
import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.receive.AbstractRabbitMqReceiver;
import com.asura.framework.rabbitmq.receive.IRabbitMqMessageLisenter;
import com.rabbitmq.client.Connection;

import java.io.IOException;
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
public abstract class AbstractRabbitQueueReceiver extends AbstractRabbitMqReceiver {

    private String queueName;

    public AbstractRabbitQueueReceiver() {

    }

    public AbstractRabbitQueueReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, String queueName) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners);
        this.queueName = queueName;
    }

    @Override
    protected void doConsumeMessage(Connection connection) throws IOException, InterruptedException {
        if(queueName == null){
            throw new BusinessException("queueName not set");
        }
        doConsumeQueueMessage(connection);
    }

    protected abstract void doConsumeQueueMessage(Connection connection)throws IOException, InterruptedException;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
