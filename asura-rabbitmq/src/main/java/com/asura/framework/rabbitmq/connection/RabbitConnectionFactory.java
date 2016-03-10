/**
 * @FileName: RabbitConnectionFactory.java
 * @Package com.asure.framework.rabbitmq
 * 
 * @author zhangshaobin
 * @created 2016年3月1日 上午10:05:54
 * 
 * Copyright 2011-2015 asura
 */
package com.asura.framework.rabbitmq.connection;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asura.framework.base.exception.BusinessException;
import com.asura.framework.base.util.Check;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * <p>
 * 连接工厂创建
 * </p>
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
public class RabbitConnectionFactory {

	private final static Logger logger = LoggerFactory.getLogger(RabbitConnectionFactory.class);

	public static final String rabbit_server = "rabbit.server";

	public static final String rabbit_server_port = "rabbit.server.port";

	public static final String rabbit_server_username = "rabbit.server.username";

	public static final String rabbit_server_password = "rabbit.server.password";

	public static final String rabbit_server_automatic_recovery_enabled = "rabbit.server.automatic_recovery_enabled";

	private PropertiesParser cfg;

	private String propSrc = null;

	private ConnectionFactory connectionFactory = null;

	private Connection connection;

	/**
	 * 
	 * 初始化
	 *
	 * @author zhangshaobin
	 * @created 2016年3月1日 下午4:34:19
	 *
	 */
	public void init() {
		initialize("rabbit.propertes");
	}

	/**
	 * 
	 * 初始化-解析配置文件
	 *
	 * @author zhangshaobin
	 * @created 2016年3月1日 下午4:34:42
	 *
	 * @param filename
	 * @throws BusinessException
	 */
	public void initialize(String filename) throws BusinessException {
		if (cfg != null) {
			return;
		}

		InputStream is = null;
		Properties props = new Properties();

		is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(filename);

		try {
			if (is != null) {
				is = new BufferedInputStream(is);
				propSrc = "the specified file : '" + filename
						+ "' from the class resource path.";
			} else {
				is = new BufferedInputStream(new FileInputStream(filename));
				propSrc = "the specified file : '" + filename + "'";
			}
			props.load(is);
		} catch (IOException ioe) {
			BusinessException initException = new BusinessException(
					"Properties file: '" + filename + "' could not be read.",
					ioe);
			throw initException;
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException ignore) {
				}
		}

		initialize(props);
	}

	/**
	 * 
	 * 初始化-解析配置文件
	 *
	 * @author zhangshaobin
	 * @created 2016年3月1日 下午4:35:53
	 *
	 * @param props
	 * @throws BusinessException
	 */
	private void initialize(Properties props) throws BusinessException {
		if (propSrc == null) {
			propSrc = "an externally provided properties instance.";
		}

		this.cfg = new PropertiesParser(props);
	}

	public ConnectionFactory getConnectFactory() {
		if (connectionFactory != null) {
			return connectionFactory;
		}
		if (initCheck()) {
			connectionFactory = new ConnectionFactory();
			connectionFactory.setHost(cfg.getStringProperty(rabbit_server));
			connectionFactory.setPort(cfg.getIntProperty(rabbit_server_port));
			connectionFactory.setUsername(cfg
					.getStringProperty(rabbit_server_username));
			connectionFactory.setPassword(cfg
					.getStringProperty(rabbit_server_password));
			connectionFactory
					.setAutomaticRecoveryEnabled(cfg
							.getBooleanProperty(rabbit_server_automatic_recovery_enabled));
		}
		return connectionFactory;
	}

	/**
	 * 
	 * 校验配置参数
	 *
	 * @author zhangshaobin
	 * @created 2016年3月1日 下午4:36:04
	 *
	 * @return
	 */
	private boolean initCheck() {
		boolean flag = true;
		if (Check.NuNStr(cfg.getStringProperty(rabbit_server))) {
			flag = false;
			String msg_error = "rabbit.server配置不能为空值!!!!";
			System.out.println(msg_error);
			logger.error(msg_error);
			return flag;
		}

		if (cfg.getIntProperty(rabbit_server_port) == 0) {
			flag = false;
			String msg_error = "rabbit.server.port配置不能为空值!!!!";
			System.out.println(msg_error);
			logger.error(msg_error);
			return flag;
		}

		if (Check.NuNStr(cfg.getStringProperty(rabbit_server_username))) {
			flag = false;
			String msg_error = "rabbit.server.username配置不能为空值!!!!";
			System.out.println(msg_error);
			logger.error(msg_error);
			return flag;
		}

		if (Check.NuNStr(cfg.getStringProperty(rabbit_server_password))) {
			flag = false;
			String msg_error = "rabbit.server.password配置不能为空值!!!!";
			System.out.println(msg_error);
			logger.error(msg_error);
			return flag;
		}
		return flag;
	}

	/**
	 * 
	 * 获取连接
	 *
	 * @author zhangshaobin
	 * @created 2016年3月1日 下午4:36:28
	 *
	 * @return
	 * @throws Exception
	 * @throws TimeoutException
	 */
	public Connection getConnection() throws Exception {
		if(connection == null){
			synchronized (this){
				if(connection == null){
					connection = getConnectFactory().newConnection();
				}
			}
		}
		return connection;
	}

	/**
	 * 
	 * 获取通道
	 *
	 * @author zhangshaobin
	 * @created 2016年3月1日 下午4:36:40
	 *
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	public Channel getChannel(Connection connection) throws IOException {
		return connection.createChannel();
	}

	/**
	 * 
	 * 关闭连接、关闭通道
	 *
	 * @author zhangshaobin
	 * @created 2016年3月1日 下午4:36:57
	 *
	 * @param channel
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void closeChannel(Channel channel)throws IOException, TimeoutException {
		if (channel != null) {
			channel.close();
		}

	}

	/**
	 *
	 * @param
	 * @throws IOException
	 */
	public void closeConnection() throws IOException {
		if (connection != null) {
			System.out.println("mq connection factory close connection");
			connection.close();
		}
	}
}
