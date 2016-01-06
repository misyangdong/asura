/**
 * @FileName: HttpClient.java
 * @Package com.asura.framework.web.http
 * 
 * @author szl
 * @created 2014年12月15日 下午4:05:56
 * 
 * Copyright 2011-2015 asura
 */
package com.asura.framework.base.util;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asura.framework.base.entity.DataTransferObject;

/**
 * <p>http客户端</p>
 * 
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 * 
 * @author szl
 * @since 1.0
 * @version 1.0
 */
public class AsuraHttpClient {

	private final Logger logger = LoggerFactory.getLogger(AsuraHttpClient.class);

	private static AsuraHttpClient instance = new AsuraHttpClient();

	private AsuraHttpClient() {
	}

	public static AsuraHttpClient getInstance() {
		return instance;
	}

	/**
	 * 
	 * get请求
	 *
	 * @author zhangshaobin
	 * @created 2014年12月15日 下午4:53:06
	 *
	 * @param url
	 * @return
	 */
	public DataTransferObject get(String url) {
		DataTransferObject dto = new DataTransferObject();
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 200000);
		int statusCode = 0;
		try {
			client.executeMethod(method);
			statusCode = method.getStatusCode();
			logger.trace("statusCode:" + statusCode);
			String data = method.getResponseBodyAsString();
			dto.putValue("data", data);
			logger.trace("data:" + data);
		} catch (Throwable t) {
			dto.setErrCode(DataTransferObject.ERROR);
			t.printStackTrace();
			logger.error("AsuraHttpClient post 请求异常 . ", t);
		} finally {
			if (statusCode == HttpStatus.SC_OK) {
				dto.setErrCode(statusCode);
				dto.setMsg("请求成功，返回" + statusCode);
			} else {
				dto.setErrCode(statusCode);
				dto.setMsg("请求失败，返回" + statusCode);
			}
			//释放链接
			method.releaseConnection();
		}
		return dto;
	}

	/**
	 * 
	 * post请求
	 *
	 * @author zhangshaobin
	 * @created 2014年12月15日 下午4:53:19
	 *
	 * @param url
	 * @param param
	 * @return
	 */
	public DataTransferObject post(String url, Map<String, String> param) {
		DataTransferObject dto = new DataTransferObject();
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url);
		if (param != null) {
			for (Entry<String, String> p : param.entrySet()) {
				method.addParameter(new NameValuePair(p.getKey(), p.getValue()));
			}
		}
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 200000);
		int statusCode = 0;
		try {
			client.executeMethod(method);
			statusCode = method.getStatusCode();
			logger.trace("statusCode:" + statusCode);
			String data = method.getResponseBodyAsString();
			dto.putValue("data", data);
			logger.trace("data:" + data);
		} catch (Throwable t) {
			statusCode = DataTransferObject.ERROR;
			t.printStackTrace();
			logger.error("AsuraHttpClient post 请求异常 . ", t);
		} finally {
			if (statusCode == HttpStatus.SC_OK) {
				dto.setErrCode(statusCode);
				dto.setMsg("请求成功，返回" + statusCode);
			} else {
				dto.setErrCode(statusCode);
				dto.setMsg("请求失败，返回" + statusCode);
			}
			//释放链接
			method.releaseConnection();
		}
		return dto;

	}
}
