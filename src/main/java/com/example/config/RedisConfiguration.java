//package com.example.config;
//
//import com.example.dto.LogRequest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
//
//@Configuration
//@EnableRedisRepositories
//public class RedisConfiguration {
//
//    @Bean
//    JedisConnectionFactory jedisConnectionFactory()
//    {
//        JedisConnectionFactory jedisConFactory
//                = new JedisConnectionFactory();
////        jedisConFactory.setHostName("localhost");
////        jedisConFactory.setPort(6379);
//        return jedisConFactory;
//    }
//
//    @Bean
//    public RedisTemplate<String, LogRequest> redisTemplate() {
//        RedisTemplate<String, LogRequest> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory());
//        template.setEnableTransactionSupport(true);
//        return template;
//    }
//
//}
