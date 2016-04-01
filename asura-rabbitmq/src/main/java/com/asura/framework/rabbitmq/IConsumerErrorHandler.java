/**
 * @FileName: IConsumerErrorHandler.java
 * @Package: com.asura.framework.rabbitmq
 * @author sence
 * @created 3/23/2016 4:09 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq;

import com.rabbitmq.client.QueueingConsumer;

/**
 * <p>消费错误处理类</p>
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
public interface IConsumerErrorHandler {

    /**
     * 消息错误处理
     *
     * @param delivery
     */
    void handlerErrorMessage(QueueingConsumer.Delivery delivery);


}
