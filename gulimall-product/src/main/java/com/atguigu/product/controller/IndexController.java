package com.atguigu.product.controller;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.service.CategoryService;
import com.atguigu.product.vo.Catalog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
/**
 * @Author qiang.long
 * @Date 2024/03/25
 * @Description
 **/
@Slf4j
@RestController
public class IndexController {

    @Resource
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * @description: p138 获取二级三级分类数据
     * @param:
     * @return: Map<String,List<Catalog2Vo>>
     **/
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson(){
        //log.info("查询二级三级菜单数据");
        //return categoryService.getCatalogJsonUseRedisWithLocalLock();
        return categoryService.getCatalogJsonWithLock();
    }


    @PostMapping("/update/category")
    public String updateCategory(@RequestBody CategoryEntity category){
        return categoryService.updateCategory(category);
    }

    /**
     * @description: 使用SpringCache缓存注解
     * @param:
     * @return: Map<List<Catalog2Vo>>
     **/
    @GetMapping("/index/springcache/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJsonUseSpringCache(){
        log.info("获取二级三级菜单数据");
        return categoryService.getCatalogJsonByDBUseSpringCache();
    }

    // 1.可重入锁 （不设过期时间）
    @GetMapping("/hello")
    public String getHello(){
        // 1. 获取一把锁，只要锁名字相同就是同一把锁，能锁住用它的所有线程
        RLock lock=redissonClient.getLock("my-lock");
        // 2.加锁
        lock.lock();  // 阻塞式等待
        try {
            log.info("线程{}获取到锁, 执行业务",Thread.currentThread().getId());
            Thread.sleep(40000);
        }catch (Exception e){

        }finally{
            // 3. 解锁 放到finally防止出现异常造成死锁
            log.info("释放锁{}",Thread.currentThread().getId());
            lock.unlock();
            return "hello";
        }
    }

    // 可重入锁 （设置过期时间）
    @GetMapping("/hello1")
    public String getHelloSetTTL(){
        // 1. 获取一把锁，只要锁名字相同就是同一把锁，能锁住用它的所有线程
        RLock lock=redissonClient.getLock("my-lock");
        // 2.加锁
        lock.lock(10, TimeUnit.SECONDS);  // 阻塞式等待 ,指定过期时间
        try {
            log.info("线程{}获取到锁",Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally{
            // 3. 解锁 放到finally防止出现异常造成死锁
            log.info("释放锁{}",Thread.currentThread().getId());
            lock.unlock();
            return "hello";
        }
    }

    // 2.读写锁
    // 写锁
    @GetMapping("/write")
    public String writeValue(){
        RReadWriteLock rReadWriteLock=redissonClient.getReadWriteLock("rw-lock");
        RLock rLock=rReadWriteLock.writeLock();  // 拿到写锁
        rLock.lock(); // 加锁
        String s=null;
        try {
            s=UUID.randomUUID().toString();
            log.info("写锁加锁成功{}",Thread.currentThread().getId());
            Thread.sleep(30000);
            stringRedisTemplate.opsForValue().set("writeValue", s);
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        } finally {
            log.info("写锁释放{}",Thread.currentThread().getId());
            rLock.unlock();
        }
        return s;
    }

    // 读锁
    @GetMapping("/read")
    public String readValue(){
        RReadWriteLock rReadWriteLock=redissonClient.getReadWriteLock("rw-lock");
        RLock rLock=rReadWriteLock.readLock();  // 拿到读锁
        rLock.lock(); // 加锁
        String s=null;
        try {
            log.info("读锁加锁成功{}",Thread.currentThread().getId());
            Thread.sleep(30000);
            s=stringRedisTemplate.opsForValue().get("writeValue");
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        } finally {
            log.info("读锁释放{}",Thread.currentThread().getId());
            rLock.unlock();
        }
        return s;
    }


    // 3.信号量模拟
    /**
     * @description: 信号量模拟（信号量即设置同时访问特定资源的线程数）：总共3个车位，没有空余其车辆就必须等待
     * @param:
     * @return: java.lang.String
     **/
    @GetMapping("/park")
    public String park() throws InterruptedException {
        // 获取信号量
        RSemaphore rSemaphore=redissonClient.getSemaphore("park");
        rSemaphore.acquire(); // 获取一个信号（车位）
        return "ok";
    }

    @GetMapping("/go")
    public String go() throws InterruptedException {
        RSemaphore rSemaphore=redissonClient.getSemaphore("park");
        rSemaphore.release(); // 释放一个信号（车位）
        return "ok";
    }

    // 4.闭锁模拟
    /**
     * 锁门：必须等5个班的人都走了
     **/
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        // 获取闭锁
        RCountDownLatch rCountDownLatch=redissonClient.getCountDownLatch("door-lock");
        log.info("获取到闭锁");
        rCountDownLatch.trySetCount(5); // 设置等待数（即5个线程都执行完完了）
        rCountDownLatch.await();// 等待闭锁都完成
        log.info("放假了");
        return "放假了";
    }

    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") Long id) {
        RCountDownLatch rCountDownLatch=redissonClient.getCountDownLatch("door-lock");
        rCountDownLatch.countDown(); // 计数减1
        log.info("{}班的人都走了",id);
        return id+"班的人都走了";
    }


}
