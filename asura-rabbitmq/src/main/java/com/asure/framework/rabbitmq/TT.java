/**
 * @FileName: TT.java
 * @Package com.asure.framework.rabbitmq
 * 
 * @author zhangshaobin
 * @created 2016年3月1日 下午6:02:30
 * 
 * Copyright 2011-2015 asura
 */
package com.asure.framework.rabbitmq;

import com.asura.framework.base.exception.BusinessException;

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
public class TT {
	
	public static void main(String []args) {
		try {
			aaa();
		} catch (BusinessException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void aaa() throws BusinessException {
		
		try {
			int i = 9/0;
		} catch(Exception e) {
			System.out.println("异常啦");
			throw new BusinessException("异常===",e);
		} finally {
			System.out.println("ddddddddddd");
		}
	}
}
