/**
 * @FileName: QueueName.java
 * @Package: com.asura.framework.rabbitmq.entity
 * @author sence
 * @created 3/12/2016 10:57 AM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq.entity;

import com.asura.framework.rabbitmq.exception.AsuraRabbitMqException;

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
public class QueueName extends NameKey {

    public QueueName(){

    }

    public QueueName(String system, String module, String function) {
        super(system, module, function);
    }

    /**
     *
     */
    public String getName() throws AsuraRabbitMqException{
        if(this.getSystem()==null ||"".equals(this.getSystem())){
            throw new AsuraRabbitMqException("QueueName:system is null");
        }
        if(this.getModule()==null || "".equals(this.getModule())){
            throw new AsuraRabbitMqException("QueueName:module is null");
        }
        if(this.getFunction()==null ||"".equals(this.getFunction())){
            throw new AsuraRabbitMqException("QueueName:function is null");
        }
        return getSystem()+"_"+getModule()+"_"+getFunction();
    }

}
