package com.atguigu.product.service.impl;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.R;
import com.atguigu.product.service.ThreadTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
/**
 * @Author qiang.long
 * @Date 2024/04/17
 * @Description
 **/
@Slf4j
@Service
public class ThreadTestServiceImpl implements ThreadTestService {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Override
    public R combineSeveralTask() throws ExecutionException, InterruptedException {
        log.info("线程池参数：核心线程数{}，最大线程数{},整体{}",threadPoolExecutor.getCorePoolSize(),threadPoolExecutor.getMaximumPoolSize(), JSON.toJSON(threadPoolExecutor));
        log.info("main....start");
        // CompletableFuture的泛型以最后一步的返回值为准
        // 任务1
        CompletableFuture<String> completableFuture1=CompletableFuture.supplyAsync(()->{
            log.info("查询商品图片信息");
            return "hello.jpg";},threadPoolExecutor);
        // 任务2
        CompletableFuture<String> completableFuture2=CompletableFuture.supplyAsync(()->{
            log.info("查询商品图片属性");
            return "黑色+256G";},threadPoolExecutor);
        // 任务3
        CompletableFuture<String> completableFuture3=CompletableFuture.supplyAsync(()->{
            log.info("查询商品介绍");
            return "华为";},threadPoolExecutor);

        // 组合
        CompletableFuture<Void> all= CompletableFuture.allOf(completableFuture1,completableFuture2,completableFuture3);
        all.get();   // 等待所有结果完成
        // 打印所有任务的执行结果
        log.info("tash1:{},task2:{},tash3:{}",completableFuture1.get(),completableFuture2.get(),completableFuture3.get());
        return R.ok().put("res",completableFuture1.get()+completableFuture2.get()+completableFuture3.get()).put("threadPoolParameters",JSON.toJSONString(threadPoolExecutor));
    }
    @Override
    public R testThreadPool(String s) throws ExecutionException, InterruptedException {
        log.info("测试自定义线程池");
        threadPoolExecutor.execute(()->{
            log.info("线程1:{}",Thread.currentThread().getId());
        });
        FutureTask<String> futureTask=new FutureTask<>(()-> {
            log.info("线程2:{}",Thread.currentThread().getId());
            return s+"ok"+Thread.currentThread().getId();
        });
        threadPoolExecutor.execute(futureTask);
        return R.ok().put("testThreadPool",futureTask.get()).put("threadPool",JSON.toJSONString(threadPoolExecutor));
    }
}
