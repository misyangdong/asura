/**
 * @FileName: AbstractRabbitMqTopicReceiver.java
 * @Package: com.asura.framework.rabbitmq.receive
 * @author sence
 * @created 3/9/2016 8:08 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.receive.topic;

import com.asura.framework.base.exception.BusinessException;
import com.asura.framework.rabbitmq.PublishSubscribeType;
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
public abstract class AbstractRabbitMqTopicReceiver extends AbstractRabbitMqReceiver {

    /**
     * 绑定key
     */
    private String bindingKey;
    /**
     * exchange 名称
     */
    private String exchangeName;
    /**
     * 发布类型
     */
    private PublishSubscribeType publishSubscribeType;


    public AbstractRabbitMqTopicReceiver(){
        super();
    }

    public AbstractRabbitMqTopicReceiver(RabbitConnectionFactory rabbitConnectionFactory, List<IRabbitMqMessageLisenter> rabbitMqMessageLiteners, String bindingKey, String exchangeName, PublishSubscribeType publishSubscribeType) {
        super(rabbitConnectionFactory, rabbitMqMessageLiteners);
        this.bindingKey = bindingKey;
        this.exchangeName = exchangeName;
        this.publishSubscribeType = publishSubscribeType;
    }

    @Override
    public void doConsumeMessage(Connection connection) throws IOException, InterruptedException{
        if (this.getBindingKey() == null) {
            throw new BusinessException("bindingKey not set");
        }
        if (this.getExchangeName() == null) {
            throw new BusinessException("exchangeName not set");
        }
        if (this.getPublishSubscribeType() == null) {
            throw new BusinessException("publishSubscribeType not set");
        }
        doConsumeTopicMessage(connection);
    }

    protected abstract void doConsumeTopicMessage(Connection connection)throws IOException, InterruptedException;

    public String getBindingKey() {
        return bindingKey;
    }

    public void setBindingKey(String bindingKey) {
        this.bindingKey = bindingKey;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public PublishSubscribeType getPublishSubscribeType() {
        return publishSubscribeType;
    }

    public void setPublishSubscribeType(PublishSubscribeType publishSubscribeType) {
        this.publishSubscribeType = publishSubscribeType;
    }
}
