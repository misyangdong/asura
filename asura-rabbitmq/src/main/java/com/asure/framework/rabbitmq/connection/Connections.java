/**
 * @FileName: Connections.java
 * @Package com.asure.framework.rabbitmq.connection
 * 
 * @author zhangshaobin
 * @created 2016年2月26日 上午10:56:52
 * 
 * Copyright 2011-2015 asura
 */
package com.asure.framework.rabbitmq.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.asura.framework.base.util.Check;
import com.asura.framework.base.util.StringUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * <p>连接对象</p>
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
@Service("connections")
public class Connections implements ApplicationContextAware {
	
	private final static Logger logger = LoggerFactory.getLogger(Connections.class);
	
	@Value("#{'${rabbit.server}'}")
	private String server;
	
	@Value("#{${rabbit.server.port}}")
	private int post;
	
	@Value("#{'${rabbit.server.username}'}")
	private String username;
	
	@Value("#{'${rabbit.server.password}'}")
	private String password;
	
	@Value("#{'${rabbit.server.automatic_recovery_enabled}'}")
	private String automaticRecoveryEnabled;
	
	private ConnectionFactory connectionFactory;
	
	
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		init();
	}
	
	/**
	 * 
	 * 初始化
	 *
	 * @author zhangshaobin
	 * @created 2016年2月26日 下午3:18:54
	 *
	 */
	public void init() {
		connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(server);
		connectionFactory.setPort(5673);
		connectionFactory.setUsername("sms_rabbit_write");
		connectionFactory.setPassword("sms_rabbit_write");
		connectionFactory.setAutomaticRecoveryEnabled(true);
	}
	
	private boolean initCheck(){
		boolean flag = true;
		if (Check.NuNStr(server)) {
			flag = false;
			String msg_error = "rabbit.server配置不能为空值!!!!";
			System.out.println(msg_error);
			logger.error(msg_error);
			return flag;
		}
		
		if (post == 0) {
			flag = false;
			String msg_error = "rabbit.server.port配置不能为空值!!!!";
			System.out.println(msg_error);
			logger.error(msg_error);
			return flag;
		}
		
		if (Check.NuNStr(username)) {
			flag = false;
			String msg_error = "rabbit.server.username配置不能为空值!!!!";
			System.out.println(msg_error);
			logger.error(msg_error);
			return flag;
		}
		
		
		return flag;
	}
	
	
	/**
	 * 
	 * 获取连接工厂
	 *
	 * @author zhangshaobin
	 * @created 2016年2月26日 上午11:00:42
	 *
	 * @return
	 */
	public ConnectionFactory getConnectionFactory() {
		return null;
	}
	
	/**
	 * 
	 * 获取连接
	 *
	 * @author zhangshaobin
	 * @created 2016年2月26日 上午10:57:38
	 *
	 * @return
	 */
	public Connection createConnection() {
		return null;
	}
	
	/**
	 * 
	 * 获取通道
	 *
	 * @author zhangshaobin
	 * @created 2016年2月26日 上午10:59:24
	 *
	 * @return
	 */
	public Channel createChannel() {
		return null;
	}

}
