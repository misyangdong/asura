/**
 * @FileName: RabbitSendOperations.java
 * @Package com.asure.framework.rabbitmq.send
 * 
 * @author zhangshaobin
 * @created 2016年2月26日 上午10:37:17
 * 
 * Copyright 2011-2015 asura
 */
package com.asure.framework.rabbitmq.send;

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
	
	String send(String msg);

}
