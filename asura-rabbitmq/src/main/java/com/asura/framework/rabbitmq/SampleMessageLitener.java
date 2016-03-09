/**
 * @FileName: SampleWorker.java
 * @Package com.asure.framework.rabbitmq
 * 
 * @author zhangshaobin
 * @created 2016年3月1日 下午2:19:10
 * 
 * Copyright 2011-2015 asura
 */
package com.asura.framework.rabbitmq;

import com.asura.framework.base.util.JsonEntityTransform;
import com.asura.framework.rabbitmq.entity.RabbitMessage;
import com.asura.framework.rabbitmq.receive.RabbitMqMessageLitener;
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
public class SampleMessageLitener implements RabbitMqMessageLitener {

	/* (non-Javadoc)
	 * @see com.asure.framework.rabbitmq.receive.IWorker#processMessage(com.rabbitmq.client.QueueingConsumer.Delivery)
	 */
	@Override
	public void processMessage(Delivery delivery) {
		String jsonStr = new String(delivery.getBody());
		RabbitMessage rm = JsonEntityTransform.json2Entity(jsonStr, RabbitMessage.class);
		System.out.println("接受者。。。。。" + rm.toJsonStr());
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
