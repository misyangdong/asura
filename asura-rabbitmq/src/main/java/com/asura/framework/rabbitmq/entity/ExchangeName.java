/**
 * @FileName: ExchangeName.java
 * @Package: com.asura.framework.rabbitmq.entity
 * @author sence
 * @created 3/12/2016 12:32 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.entity;

import com.asura.framework.rabbitmq.exception.AsuraRabbitMqException;

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
public class ExchangeName extends QueueName {

    public ExchangeName() {

    }

    public ExchangeName(String system, String module, String function) {
        super(system, module, function);
    }

    /**
     *
     */
    public String getName() throws AsuraRabbitMqException {
        if (this.getSystem() == null || "".equals(this.getSystem())) {
            throw new AsuraRabbitMqException("QueueName:system is null");
        }
        if (this.getModule() == null || "".equals(this.getModule())) {
            throw new AsuraRabbitMqException("QueueName:module is null");
        }
        if (this.getFunction() == null || "".equals(this.getFunction())) {
            throw new AsuraRabbitMqException("QueueName:function is null");
        }
        return "EX_" + getSystem() + "_" + getModule() + "_" + getFunction();
    }

    /**
     * 根据环境获取到Name
     *
     * @param environment
     * @return
     * @throws AsuraRabbitMqException
     */
    public String getNameByEnvironment(String environment) throws AsuraRabbitMqException {
        if (environment == null) {
            return getName();
        }
        return environment + "." + getName();
    }
}
