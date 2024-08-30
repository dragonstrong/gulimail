package com.atguigu.product.thread;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
/**
 * @Author qiang.long
 * @Date 2024/04/22
 * @Description 多线程和单线程效率测试
 **/
@Slf4j
public class CaculateTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //mutiThread();
        //sigThread();
        //WidgetA a=new WidgetA();
        //a.doSomething();

        MyHAHA myHAHA=t-> t*t;

        System.out.println(myHAHA.haha(2));

    }


    private static void mutiThread() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(12,30,10, TimeUnit.MICROSECONDS,new LinkedBlockingQueue<>(100), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        ArrayList<CompletableFuture<Long>> list=new ArrayList<>();
        LocalDateTime startTime=LocalDateTime.now();
        log.info("开始");
        for(int k=0;k<10;k++){
            CompletableFuture<Long> completableFuture=CompletableFuture.supplyAsync(()->{
                //log.info("任务开始执行，线程id{}",Thread.currentThread().getId());
                long sum=0;
                for(int i=0;i<100000;i++){
                    for(int j=0;j<100000;j++){
                        sum +=i+j;
                    }
                }
                //log.info("线程{}结束",Thread.currentThread().getId());
                return sum;
            },threadPoolExecutor);
            list.add(completableFuture);
        }

        CompletableFuture<Void> all=CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
        all.get();
        LocalDateTime endTime=LocalDateTime.now();
        long sec=Duration.between(startTime,endTime).getSeconds();
        log.info("结束,耗时{}s",sec);
    }

    private static void redis() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(12,30,10, TimeUnit.MICROSECONDS,new LinkedBlockingQueue<>(100), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        ArrayList<CompletableFuture<Long>> list=new ArrayList<>();
        LocalDateTime startTime=LocalDateTime.now();
        log.info("开始");
        int count=0;
        List<String> ipPools=new ArrayList<>();
        for(int k=0;k<10;k++){
            CompletableFuture<Long> completableFuture=CompletableFuture.supplyAsync(()->{
                //log.info("任务开始执行，线程id{}",Thread.currentThread().getId());
                long sum=0;
                for(int i=0;i<100000;i++){
                    for(int j=0;j<100000;j++){
                        sum +=i+j;
                    }
                }
                //log.info("线程{}结束",Thread.currentThread().getId());
                return sum;
            },threadPoolExecutor);
            list.add(completableFuture);
        }
        BiConsumer<Integer,Long> biConsumer=(m,l)->{
            m++;
        };



        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        /*
        list.stream().map(CompletableFuture::join).forEach((count,l)->{
            count++;
        });

         */
        //all.get();

        LocalDateTime endTime=LocalDateTime.now();
        long sec=Duration.between(startTime,endTime).getSeconds();
        log.info("结束,耗时{}s",sec);
    }


    public static void sigThread(){
        log.info("开始");
        for(int k=0;k<10;k++){
            long sum=0;
            for(int i=0;i<100000;i++){
                for(int j=0;j<100000;j++){
                    sum +=i+j;
                }
            }
        }
        log.info("结束");
    }

    public static String dateFormat(LocalDateTime localDateTime){
        // 创建日期时间格式化器，指定要使用的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化当前时间为指定格式
        return localDateTime.format(formatter);
    }
}

class Widget{
    public synchronized void doSomething(){
        System.out.println("widget: doSomething");
    }
}


class WidgetA extends Widget{
    public synchronized void doSomething(){
        System.out.println("widgetA: doSomething");
        super.doSomething();
    }
}

interface MyHAHA{
    int haha(int i);
}