package com.atguigu.product.config;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
/**
 * @Author qiang.long
 * @Date 2024/03/29
 * @Description 自定义SpringCache缓存配置
 * @EnableCaching可以从主类移到这里
 *
 * @EnableConfigurationProperties 绑定配置文件
 **/
@EnableConfigurationProperties(CacheProperties.class)
@EnableCaching
@Configuration
public class MyCacheConfig {
    // 会自动从容器中拿到参数CacheProperties cacheProperties
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){
        RedisCacheConfiguration config=RedisCacheConfiguration.defaultCacheConfig();
        config=config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));  // 指定键的序列化方式，都是String
        config=config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));  // 指定值的序列化方式，通用json
        // 将application.yml中的配置也导入
        CacheProperties.Redis redisProperties=cacheProperties.getRedis();  // 和redis相关的所有配置
        if (redisProperties.getTimeToLive()!=null){ // 过期时间
            config=config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix()!=null){ // key前缀
            config=config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()){ // 是否缓存空值
            config=config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()){ // 是否使用key前缀
            config=config.disableKeyPrefix();
        }
        return config;
    }
}
