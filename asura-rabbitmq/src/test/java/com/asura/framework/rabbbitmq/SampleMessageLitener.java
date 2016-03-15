/**
 * @FileName: SampleMessageLitener.java
 * @Package: com.asura.framework.rabbbitmq
 * @author sence
 * @created 3/14/2016 9:07 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbbitmq;

import com.asura.framework.rabbitmq.receive.IRabbitMqMessageLisenter;
import com.rabbitmq.client.QueueingConsumer;

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
public class SampleMessageLitener implements IRabbitMqMessageLisenter {


    @Override
    public void processMessage(QueueingConsumer.Delivery delivery) {
        System.out.println("[receive:"+new String(delivery.getBody())+"]");
    }
}
