/**
 * @FileName: InitPublisher.java
 * @Package com.asura.framework.conf.publish
 * 
 * @author zhangshaobin
 * @created 2013-6-27 下午2:26:59
 * 
 * Copyright 2011-2015 asura
 */
package com.asura.framework.conf.publish;

import com.asura.framework.conf.publish.ConfigPublisher;

/**
 * <p>初始化发布者</p>
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
public class InitPublisher {
	
	private InitPublisher() {}
	
	/**
	 * 
	 * 初始化
	 *
	 * @author zhangshaobin
	 * @created 2013-6-27 下午2:27:37
	 *
	 */
	public void init() {
		ConfigPublisher.getInstance();
	}

}
