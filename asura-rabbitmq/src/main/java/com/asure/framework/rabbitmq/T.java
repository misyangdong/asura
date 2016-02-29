package com.asure.framework.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class T {
	
	int i;

	public static void main(String[] args) throws InterruptedException {
		System.out.println(new T().i);
//		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/spring/springRabbitMQ.xml");
//		RabbitTemplate template = (RabbitTemplate)ctx.getBean("amqpTemplate");
//		while(true){
//			template.convertAndSend("Hello, world!");
//		}
//		Thread.sleep(100000000);
//		ctx.destroy();

	}

}
