/**
 * @FileName: ConfigPublisher.java
 * @Package com.sfbest.config.publish
 * 
 * @author zhangshaobin
 * @created 2013-6-26 上午10:50:04
 * 
 * Copyright 2011-2015 asura
 */
package com.asura.framework.conf.publish;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.ConfigUtils;

/**
 * <p>配置信息发布</p>
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
public class ConfigPublisher {

	final static Logger logger = LoggerFactory.getLogger("ConfigPublisher.class");

	public final String zkDataPath = "/AsuraConfig";

	private ZkClient client;

	private static ConfigPublisher pub;

	private ConfigPublisher() {
		init();
	}

	/**
	 * 
	 * 获取ConfigPublisher实例
	 *
	 * @author zhangshaobin
	 * @created 2013-6-26 上午10:55:04
	 *
	 * @return	ConfigPublisher实例
	 */
	public static synchronized ConfigPublisher getInstance() {
		if (null != pub)
			return pub;
		pub = new ConfigPublisher();
		return pub;
	}

	/**
	 * 
	 * 初始化
	 *
	 * @author zhangshaobin
	 * @created 2013-6-26 上午10:55:30
	 *
	 */
	private void init() {
		String address = ConfigUtils.getProperty("dubbo.registry.address");
		String applicationName = ConfigUtils.getProperty("dubbo.application.name");
		int connectionTimeout = 60000;
		String zookeeperServer = null;
		if (address.startsWith("zookeeper") && address.length() > 20) {
			zookeeperServer = address.replace("\\", "").replace("zookeeper://", "").replace("?backup=", ",");
			try {
				client = new ZkClient(zookeeperServer, connectionTimeout);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("连接配置中心服务器超时，时间" + connectionTimeout + "毫秒。", e.getCause());
				System.exit(1);
			}
			System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ").format(new Date()) + applicationName
					+ " connected to cofnig server(" + zookeeperServer + ").");
			logger.info(applicationName + " connected to cofnig server(" + zookeeperServer + ").");
		}
	}

	/**
	 * 
	 * 增加配置信息
	 *
	 * @author zhangshaobin
	 * @created 2013-6-27 下午3:04:08
	 *
	 * @param type
	 * @param code
	 * @param data
	 */
	public void setConfig(String type, String code, String data) {
		String path = zkDataPath;
		if (!client.exists(path)) {
			client.createPersistent(path);
		}

		path += "/" + type;
		if (!client.exists(path)) {
			client.createPersistent(path);
		}

		path += "/" + code;
		if (!client.exists(path)) {
			client.createPersistent(path);
		}

		client.writeData(path, data);
	}

	/**
	 * 
	 * 删除配置信息
	 *
	 * @author zhangshaobin
	 * @created 2013-6-27 下午3:04:23
	 *
	 * @param type
	 * @param code
	 */
	public void deleteConfig(String type, String code) {
		String path = zkDataPath + "/" + type + "/" + code;
		if (client.exists(path)) {
			client.delete(path);
		}
	}

	/**
	 * 
	 * 从zookeeper上获取配置信息
	 *
	 * @author zhangshaobin
	 * @created 2013-6-28 下午1:56:52
	 *
	 * @param type	配置信息类型
	 * @param code	配置信息编码
	 * @return	配置信息值
	 */
	public String getConfigValue(String type, String code) {
		String path = zkDataPath + "/" + type + "/" + code;
		if (!client.exists(path)) {
			return "配置项不存在";
		}
		return client.readData(path);
	}

}
