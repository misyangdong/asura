/**
 * @FileName: RabbitSendClient.java
 * @Package com.asure.framework.rabbitmq.send
 * 
 * @author zhangshaobin
 * @created 2016年2月29日 下午9:50:55
 * 
 * Copyright 2011-2015 asura
 */
package com.asura.framework.rabbitmq.send;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asura.framework.base.exception.BusinessException;
import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.entity.RabbitMessage;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

/**
 * <p>rabbitmq消息生产端</p>
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
public class RabbitSendClient implements RabbitSendOperations{
	
	private final static Logger logger = LoggerFactory.getLogger(RabbitSendClient.class);
	
	private RabbitConnectionFactory rabbitConnectionFactory;
	

	/**
	 * @return the rabbitConnectionFactory
	 */
	public RabbitConnectionFactory getRabbitConnectionFactory() {
		return rabbitConnectionFactory;
	}


	/**
	 * @param rabbitConnectionFactory the rabbitConnectionFactory to set
	 */
	public void setRabbitConnectionFactory(
			RabbitConnectionFactory rabbitConnectionFactory) {
		this.rabbitConnectionFactory = rabbitConnectionFactory;
	}


	/**
	 * 
	 * 发送消息-queue方式
	 *
	 * @author zhangshaobin
	 * @created 2016年3月1日 下午4:39:23
	 *
	 * @param queueName 格式为：系统标示_模块标示_功能标示
	 * @param msg 具体消息
	 */
	public void sendQueue(String queueName, String msg) throws BusinessException{
		//加入cat
		//创建连接
	    //描述队列
	   	//发送消息
	   	//关闭连接、关闭通道
	   	Transaction tran = Cat.newTransaction("rabbitmq-send", queueName);
	   	Connection connection = null;
	   	Channel channel = null;
	   	try {
			RabbitMessage rm = new RabbitMessage();
			rm.setData(msg);
			rm.setType(queueName);
			connection = rabbitConnectionFactory.getConnection();
			channel = rabbitConnectionFactory.getChannel(connection);
			channel.queueDeclare(queueName, true, false, false, null);
			channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, rm.toJsonStr().getBytes());
			Cat.logMetricForCount(queueName); // 统计请求次数, 可以查看对应队列中放入了多少信息
			tran.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			String err = queueName + "  rabbitmq发送消息异常";
			Cat.logError(err, e);
			tran.setStatus(e);
			throw new BusinessException(err, e);
		} finally {
			try {
				rabbitConnectionFactory.close(connection, channel);
			} catch (IOException e) {
				logger.error("关闭rabbitmq连接异常", e);
			} catch (TimeoutException e) {
				logger.error("关闭rabbitmq连接异常", e);
			}
			tran.complete();
		}
	}
	
    
	 /**
     * 
     * 发送消息-topic方式
     *
     * @author zhangshaobin
     * @created 2016年3月1日 下午4:40:59
     *
     * @param queueName 格式为：系统标示_模块标示_功能标示
     * @param msg 具体消息
     */
	public void sendTopic(String queueName, String msg){
		//加入cat
		//创建连接
	    //描述队列
	   	//发送消息
	   	//关闭连接、关闭通道
	   	Transaction tran = Cat.newTransaction("rabbitmq-sendTopic", queueName);
	   	Connection connection = null;
	   	Channel channel = null;
	   	try {
			RabbitMessage rm = new RabbitMessage();
			rm.setData(msg);
			rm.setType(queueName);
			connection = rabbitConnectionFactory.getConnection();
			channel = rabbitConnectionFactory.getChannel(connection);
			channel.queueDeclare(queueName, true, false, false, null);
			channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, rm.toJsonStr().getBytes());
			Cat.logMetricForCount(queueName); // 统计请求次数, 可以查看对应队列中放入了多少信息
			tran.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			String err = queueName + "  rabbitmq发送消息异常";
			Cat.logError(err, e);
			tran.setStatus(e);
			throw new BusinessException(err, e);
		} finally {
			try {
				rabbitConnectionFactory.close(connection, channel);
			} catch (IOException e) {
				logger.error("关闭rabbitmq连接异常", e);
			} catch (TimeoutException e) {
				logger.error("关闭rabbitmq连接异常", e);
			}
			tran.complete();
		}
	} 

}
