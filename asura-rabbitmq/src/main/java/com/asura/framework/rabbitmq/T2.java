package com.asura.framework.rabbitmq;

import com.asura.framework.rabbitmq.send.RabbitSendOperations;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class T2 {
	

	public static void main(String[] args) throws Exception {
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/spring/spring2.xml");
		RabbitSendOperations rabbitSendOperations = (RabbitSendOperations)ctx.getBean("rabbitSendClient");
		for (int i=0; i<10; i++) {
			rabbitSendOperations.sendQueue("dayu_004", "dayu_004 msgT2T2T2T2T2");
		}
		Thread.sleep(100000000);
		ctx.destroy();

	}

}
