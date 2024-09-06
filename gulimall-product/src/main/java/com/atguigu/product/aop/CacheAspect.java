package com.atguigu.product.aop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
/**
 * @Author qiang.long
 * @Date 2024/09/06
 * @Description 删缓存切面
 **/
@Slf4j
@Aspect
@Component
public class CacheAspect {
    private static final String CATALOG_JSON = "catalogJson";
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * @description: 切点: 所有被@DeleteCache注解标注的方法
     **/
    @Pointcut("@annotation(com.atguigu.product.valid.DeleteCache)")
    public void deleteCacheLayer(){
    }
    /**
     * @description: 后置通知：目标方法执行之后执行
     **/
    @After("deleteCacheLayer()")
    public void deleteCache(){
        try {
            log.info("修改分类数据，删除缓存...");
            stringRedisTemplate.delete(CATALOG_JSON);
        }catch (Exception e){
            log.info("删除redis缓存失败，key={}",CATALOG_JSON);
        }
    }


}
