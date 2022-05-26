package com.example.config;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;

/**
 * Redis connection configuration using Jedis.
 *
 * @author Mark Paluch
 * @author Stephane Nicoll
 */
@Configuration
@ConditionalOnClass({ GenericObjectPool.class, JedisConnection.class, Jedis.class })
class JedisConnectionConfiguration extends RedisConnectionConfiguration {

    private final RedisProperties properties;

    private final List<JedisClientConfigurationBuilderCustomizer> builderCustomizers;

    JedisConnectionConfiguration(RedisProperties properties,
                                 ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                                 ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
                                 ObjectProvider<List<JedisClientConfigurationBuilderCustomizer>> builderCustomizers) {
        super(properties, sentinelConfiguration, clusterConfiguration);
        this.properties = properties;
        this.builderCustomizers = builderCustomizers
                .getIfAvailable(Collections::emptyList);
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public JedisConnectionFactory redisConnectionFactory() throws UnknownHostException {
        return createJedisConnectionFactory();
    }

    private JedisConnectionFactory createJedisConnectionFactory() {
        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration();
        if (getSentinelConfig() != null) {
            return new JedisConnectionFactory(getSentinelConfig(), clientConfiguration);
        }
        if (getClusterConfiguration() != null) {
            return new JedisConnectionFactory(getClusterConfiguration(),
                    clientConfiguration);
        }
        return new JedisConnectionFactory(getStandaloneConfig(), clientConfiguration);
    }

    private JedisClientConfiguration getJedisClientConfiguration() {
        JedisClientConfigurationBuilder builder = applyProperties(
                JedisClientConfiguration.builder());
        RedisProperties.Pool pool = this.properties.getJedis().getPool();
        if (pool != null) {
            applyPooling(pool, builder);
        }
        if (StringUtils.hasText(this.properties.getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        customize(builder);
        return builder.build();
    }

    private JedisClientConfigurationBuilder applyProperties(
            JedisClientConfigurationBuilder builder) {
        if (this.properties.isSsl()) {
            builder.useSsl();
        }
        if (this.properties.getTimeout() != null) {
            Duration timeout = this.properties.getTimeout();
            builder.readTimeout(timeout).connectTimeout(timeout);
        }
        return builder;
    }

    private void applyPooling(RedisProperties.Pool pool,
                              JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }

    private JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getMaxWait() != null) {
            config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        return config;
    }

    private void customizeConfigurationFromUrl(
            JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = parseUrl(this.properties.getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    private void customize(
            JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        for (JedisClientConfigurationBuilderCustomizer customizer : this.builderCustomizers) {
            customizer.customize(builder);
        }
    }

}