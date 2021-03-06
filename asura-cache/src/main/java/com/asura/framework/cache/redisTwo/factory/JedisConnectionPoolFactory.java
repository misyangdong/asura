package com.asura.framework.cache.redisTwo.factory;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public final class JedisConnectionPoolFactory implements InitializingBean, FactoryBean<ShardedJedis> {

	private String host = "localhost";

	private int port = Protocol.DEFAULT_PORT;

	private String password;

	private int timeout = Protocol.DEFAULT_TIMEOUT;

	private JedisPoolConfig poolConfig;

	private List<JedisShardInfo> shards;

	private ShardedJedisPool jedisPool;

	public void setPoolConfig(final JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}

	public void setShards(final List<JedisShardInfo> shardInfos) {
		this.shards = shardInfos;
	}

	@Override
	public ShardedJedis getObject() throws Exception {
		final ShardedJedis jedis = jedisPool.getResource();
		System.out.println("========" + jedis);
		return jedis;
	}

	@Override
	public Class<?> getObjectType() {
		return ShardedJedis.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (poolConfig == null) {
			poolConfig = new JedisPoolConfig();
		}
		if (shards == null || shards.isEmpty()) {
			final JedisShardInfo shard = new JedisShardInfo(host, port);
			if (StringUtils.hasText(password)) {
				shard.setPassword(password);
			}
			if (timeout > 0) {
				shard.setTimeout(timeout);
			}
			shards = Collections.singletonList(shard);
		}
		jedisPool = new ShardedJedisPool(poolConfig, shards);
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}
}
