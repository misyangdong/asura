/**
 * @FileName: Cache.java
 * @Package com.asura.framework.cache.redisOne
 * 
 * @author zhangshaobin
 * @created 2014年11月30日 上午12:31:21
 * 
 * Copyright 2011-2015 asura
 */
package com.asura.framework.cache.redisOne;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>缓存注解</p>
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
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

	DataStructure dataStructure() default DataStructure.hash;

	String key();

	String fieldKey() default "";

	int expireTime() default 3600;

	boolean selfControl() default false; // true缓存的使用不受外部控制
	
	boolean isUpdate() default false; // true 更新缓存中的数据

}
