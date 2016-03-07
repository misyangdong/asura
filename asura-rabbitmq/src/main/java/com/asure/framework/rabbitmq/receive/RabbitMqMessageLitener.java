/**
 * @FileName: IWorker.java
 * @Package com.asure.framework.rabbitmq.receive
 * 
 * @author zhangshaobin
 * @created 2016年3月1日 下午2:02:02
 * 
 * Copyright 2011-2015 asura
 */
package com.asure.framework.rabbitmq.receive;

import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 * <p>TODO</p>
 * 
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 * 
 * @author zhangshaobin
 * @since 1.0
 * @version 1.0
 */
public interface RabbitMqMessageLitener {
	
	void processMessage(Delivery delivery);

}
