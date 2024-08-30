package com.atguigu.product.thread;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @Author qiang.long
 * @Date 2024/04/01
 * @Description 测试CompletableFuture
 * 统一使用指定线程池的方式创建
 **/
@Slf4j
public class CompletableFutureTest {
    static ExecutorService service=Executors.newFixedThreadPool(50);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        combineSeveralTask();
    }

    /**
     * @description: 无返回值
     **/

    public static void run(){
        log.info("main....start");
        CompletableFuture<Void> completableFuture=CompletableFuture.runAsync(()->{
            log.info("当前线程：{}",Thread.currentThread().getId());
            int i=10/2;
            log.info("运行结果:{}",i);},service);
            log.info("main....end");
    }

    /**
     * @description: 有返回值
     **/
    public static void supply() throws ExecutionException, InterruptedException {
        log.info("main....start");
        CompletableFuture<Integer> completableFuture=CompletableFuture.supplyAsync(()->{
            log.info("当前线程：{}",Thread.currentThread().getId());
            int i=10/2;
            log.info("运行结果:{}",i);
            return i;},service);
        // 使用get获取异步任务返回值
        log.info("main....end,res: {}",completableFuture.get());
    }



    /**
     * @description: 完成时回调
     * @param:
     * @return: void
     **/
    public static void supply1() throws ExecutionException, InterruptedException {
        log.info("main....start");
        CompletableFuture<Integer> completableFuture=CompletableFuture.supplyAsync(()->{
            log.info("当前线程：{}",Thread.currentThread().getId());
            int i=10/0;
            return i;},service).whenComplete((res,execption)->{
                // whenComplete能得到异常信息，但没法修改返回数据
            log.info("结果是: {}, 异常是",res,execption);
        }).exceptionally(throwable -> {
            // exceptionally 可以感知异常并返回默认值
            return -1;
        });
        // 获取返回值
        Integer exe=completableFuture.get();
        log.info("main....end,  {}",exe);
    }

    /**
     * @description: handle方法 可处理结果和异常  同时有返回值
     **/
    public static void handle() throws ExecutionException, InterruptedException {
        log.info("main....start");
        CompletableFuture<Integer> completableFuture=CompletableFuture.supplyAsync(()->{
            log.info("当前线程：{}",Thread.currentThread().getId());
            int i=10/2;
            return i;},service).handle((res,execption)->{
                if(res!=null){
                    log.info("结果是：{}",res);
                    return res*res;  // 处理执行结果
                }
                if(execption!=null){
                    log.info("异常是: {}",execption);
                    return -1;
                }
            return -2;
        });
        // 获取返回值
        Integer exe=completableFuture.get();
        log.info("main....end,  return: {}",exe);
    }


    // 线程串行化

    public static void combine() throws ExecutionException, InterruptedException {
        log.info("main....start");
        // CompletableFuture的泛型以最后一步的返回值为准
        CompletableFuture<String> completableFuture=CompletableFuture.supplyAsync(()->{
            log.info("任务1启动了，当前线程id: {}",Thread.currentThread().getId());
            int i=10/2;
            return i;},service).thenApplyAsync(res->{
                log.info("任务1的结果:{}",res);
                log.info("任务2启动了, 当前线程id: {}",Thread.currentThread().getId());
                return "hello"+res;  // 接收任务1的结果并处理返回
        },service);
        // 获取返回值
        String exe=completableFuture.get();
        log.info("main....end,  return: {}",exe);
    }


    /**
     * @description: 组合两个任务-2个都完成才执行任务3
     **/
    public static void combineTask() throws ExecutionException, InterruptedException {
        log.info("main....start");
        // CompletableFuture的泛型以最后一步的返回值为准
        CompletableFuture<Integer> completableFuture1=CompletableFuture.supplyAsync(()->{
            log.info("任务1启动了，当前线程id: {}",Thread.currentThread().getId());
            int i=10/4;
            return i;},service);

        CompletableFuture<Integer> completableFuture2=CompletableFuture.supplyAsync(()->{
            log.info("任务2启动了，当前线程id: {}",Thread.currentThread().getId());
            int i=10/3;
            return i;},service);
        // 组合2个任务
        CompletableFuture<String> twoTask=completableFuture1.thenCombine(completableFuture2,(res1,res2)->{
            log.info("任务3启动了，当前线程id: {}",Thread.currentThread().getId());
            log.info("组合两个任务");
            return "result1:"+res1+",result2:"+res2;
        });
        String  exe=twoTask.get();
        log.info("main....end,  return: {}",exe);
    }


    /**
     * @description: 组合两个任务-2个中有1个完成就执行任务3
     **/
    public static void combineTaskEither() throws ExecutionException, InterruptedException {
        log.info("main....start");
        // CompletableFuture的泛型以最后一步的返回值为准
        CompletableFuture<Integer> completableFuture1=CompletableFuture.supplyAsync(()->{
            log.info("任务1启动了，当前线程id: {}",Thread.currentThread().getId());
            int i=10/4;
            try {
                Thread.sleep(5000); //休眠5s
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("任务1结束");
            return i;},service);

        CompletableFuture<Integer> completableFuture2=CompletableFuture.supplyAsync(()->{
            log.info("任务2启动了，当前线程id: {}",Thread.currentThread().getId());
            int i=10/3;
            return i;},service);
        // 组合2个任务
        CompletableFuture<String> twoTask=completableFuture1.applyToEither(completableFuture2,res->{
            log.info("任务3启动了，当前线程id: {}",Thread.currentThread().getId());
            log.info("前两个任务中执行最快的结果:{}",res);
            log.info("任务3结束");
            return "result:"+res*res;
        });
        String  exe=twoTask.get();
        log.info("main....end,  return: {}",exe);
    }


    /**
     * @description: 多任务组合
     **/
    public static void combineSeveralTask() throws ExecutionException, InterruptedException {
        log.info("main....start");
        // CompletableFuture的泛型以最后一步的返回值为准
        // 任务1
        CompletableFuture<String> completableFuture1=CompletableFuture.supplyAsync(()->{
            log.info("查询商品图片信息");
            return "hello.jpg";},service);
        // 任务2
        CompletableFuture<String> completableFuture2=CompletableFuture.supplyAsync(()->{
            log.info("查询商品图片属性");
            return "黑色+256G";},service);
        // 任务3
        CompletableFuture<String> completableFuture3=CompletableFuture.supplyAsync(()->{
            log.info("查询商品介绍");
            return "华为";},service);

        // 组合
        CompletableFuture<Void> all= CompletableFuture.allOf(completableFuture1,completableFuture2,completableFuture3);
        all.get();   // 等待所有结果完成
        // 打印所有任务的执行结果
        log.info("tash1:{},task2:{},tash3:{}",completableFuture1.get(),completableFuture2.get(),completableFuture3.get());

    }

}
