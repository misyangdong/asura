/**
 * @FileName:
 * @Package: com.asura.services.log
 *
 * @author sence
 * @created 11/3/2014 6:36 PM
 *
 * Copyright 2011-2015 Asura
 */
package com.asura.framework.logback;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.rpc.RpcContext;

/**
 *
 * <p>系统日志AOP</p>
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author sence
 * @since 1.0
 * @version 1.0
 */
@Component
@Aspect
public class SystemLogger {

	private static final Logger logger = LoggerFactory.getLogger(SystemLogger.class);

	/** 客户地址 */
	private final static String HOST = "address";

	/** 调用的接口 */
	private final static String INTERFACE = "service";

	/** 调用的方法名称 */
	private final static String METHOD = "method";


	@Around("execution(* com.ziroom..*.proxy.*.* (..))")
	public Object doBasicProfiling(final ProceedingJoinPoint joinPoint) throws Throwable {
		
		if (RpcContext.getContext().getRemoteAddressString() != null && RpcContext.getContext().getMethodName() != null
				&& RpcContext.getContext().getUrl() != null) {
			MDC.put(HOST, RpcContext.getContext().getRemoteAddressString());
			MDC.put(INTERFACE, RpcContext.getContext().getUrl().getServiceInterface());
			MDC.put(METHOD, RpcContext.getContext().getMethodName());
		} else {
			MDC.put(HOST, "127.0.0.1");
			MDC.put(INTERFACE, "none");
			MDC.put(METHOD, "none");
		}

		final DataLogEntity de = new DataLogEntity();
		de.setClassName(joinPoint.getSignature().getDeclaringTypeName());
		de.setMethodName(joinPoint.getSignature().getName());
		de.setParams(joinPoint.getArgs());
		// 参数日志
		if (logger.isDebugEnabled()) {
			logger.debug(de.toJsonStr());
		}
		try {
			long start = System.currentTimeMillis();
			final Object retVal = joinPoint.proceed();
			long end = System.currentTimeMillis();
			// 记录耗时
			logger.info(de.toJsonStr()+" 耗时:" + (end - start) + " ms");
			return retVal;
		} catch (final Exception e) {
			final ErrorLogEntity ele = new ErrorLogEntity(de);
			ele.setThrowMessage(e.toString());
			//抛出异常日志
			logger.error(ele.toJsonStr());
			throw e;
		} finally {
			MDC.remove(HOST);
			MDC.remove(INTERFACE);
			MDC.remove(METHOD);
		}
	}
}