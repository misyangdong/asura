package com.asure.framework.rabbitmq;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.asure.framework.rabbitmq.send.RabbitSendOperations;

public class T {
	

	public static void main(String[] args) throws Exception {
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/spring/spring.xml");
		Thread.sleep(100000000);
		ctx.destroy();

	}

}
