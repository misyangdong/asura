/**
 * @FileName: RabbitSendOperations.java
 * @Package com.asure.framework.rabbitmq.send
 * 
 * @author zhangshaobin
 * @created 2016年2月26日 上午10:37:17
 * 
 * Copyright 2011-2015 asura
 */
package com.asura.framework.rabbitmq.send;

import com.asura.framework.base.exception.BusinessException;

/**
 * <p>发送操作接口</p>
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
public interface RabbitSendOperations {
	
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
    public void sendQueue(String queueName, String msg) throws Exception;
	
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
	public void sendTopic(String queueName, String msg) throws BusinessException;

}
