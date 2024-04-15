package com.atguigu.product.thread;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
/**
 * @Author qiang.long
 * @Date 2024/03/29
 * @Description 线程测试 ：初始化线程的4种方式
 **/
@Slf4j
public class ThreadTest {

    // 保证当前系统中只有一个两个线程池（分核心业务和非核心业务），每个异步任务直接提交给线程池
    public static ExecutorService service=Executors.newFixedThreadPool(10); //创建一个包含10个线程的线程池
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        log.info("main....start");
        service.submit(new Callable01());
        log.info("main....end");
        /**
         * 线程池参数
         * int corePoolSize: 核心线程数，线程池创建好后就准备就绪，等待接受异步任务去执行（相当于new 了很多Thread但没有start, 当系统给线程池提交任务后就会start）。一直在，除非设置了allowCoreThreadTimeOut
         * int maximumPoolSize: 最大线程数量，控制资源并发数
         * long keepAliveTime: 存活时间，非核心线程的最大等待时间，若还没有任务来执行就释放
         * TimeUnit unit: 存活时间的单位
         * BlockingQueue<Runnable> workQueue： 阻塞队列。如果任务很多就会将多的任务放到队列，等待线程空闲再取出执行。一定根据压测设置合适的大小，否则容易撑爆内存。
         * ThreadFactory threadFactory: 线程的创建工厂
         * RejectedExecutionHandler handler: 如果队列满了，按照指定的拒绝策略执行任务
         *
         **/
        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(1,1,10,TimeUnit.SECONDS,new LinkedBlockingQueue<>(1000),Executors.defaultThreadFactory(),new ThreadPoolExecutor.DiscardPolicy());
    }


    /**
     * 1.继承Thread： 重写run方法，调用start启动
     **/
    public static class Thread1 extends Thread{
        @Override
        public void run(){
            log.info("当前线程：{}",Thread.currentThread().getId());
            int i=10/2;
            log.info("运行结果:{}",i);
        }
    }


    /**
     * 2.实现Runnable接口： 实现run方法，调用start启动
     **/

    public static class Runable01 implements Runnable{
        @Override
        public void run() {
            log.info("当前线程：{}",Thread.currentThread().getId());
            int i=10/2;
            log.info("运行结果:{}",i);
        }
    }

    /**
     * 3.实现Callable接口（带泛型）： 重写run方法
     **/

    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            log.info("当前线程：{}",Thread.currentThread().getId());
            int i=10/2;
            log.info("运行结果:{}",i);
            return i;
        }
    }

    /**
     * 4.线程池
     **/



}
