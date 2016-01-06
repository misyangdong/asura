/**
 * @FileName: EncryptionUtil.java
 * @Package com.asura.framework.util
 * 
 * @author zhangshaobin
 * @created 2012-12-18 下午5:04:40
 * 
 * Copyright 2011-2015 asura
 */
package com.asura.framework.base.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>加密工具类</p>
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
public class EncryptionUtil {
	
	/**
	 * 
	 * 采用MD5方式加密字符串
	 *
	 * @author zhangshaobin
	 * @created 2012-12-18 下午5:07:45
	 *
	 * @param source	要加密的字符串
	 * @return	加密后的结果
	 */
	public static String MD5(String source) {
		// 32位加密md
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(source.getBytes()); // 更新
		byte[] bt = md.digest(); // 摘要

		// 保留结果的字符串
		StringBuilder sb = new StringBuilder();
		int p = 0;
		for (int i = 0; i < bt.length; i++) {
			p = bt[i];
			if (p < 0)
				p += 256; // 负值时的处理
			if (p < 16)
				sb.append("0");
			sb.append(Integer.toHexString(p));// 转换成16进制
		}
		return sb.toString();
	}

}
