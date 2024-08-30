package com.atguigu.product.thread;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
/**
 * @Author qiang.long
 * @Date 2024/05/02
 * @Description
 **/
@Slf4j
public class TestPool {
    public static void main(String[] args){
        ReentrantLock reentrantLock=new ReentrantLock(); // 非公平锁
        // A B C 三个顾客去银行办理业务，A先到， 此时窗口空无一人，他优先获得办理窗口的机会，办理业务
        ConcurrentHashMap<String,String> map=new ConcurrentHashMap<>();
        map.put("1","2");

        // A耗时最长，估计长期占有窗口
        new Thread(()->{
            reentrantLock.lock();
            try {
                log.info("-----come in A");
                TimeUnit.MINUTES.sleep(20);
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                reentrantLock.unlock();
            }
        },"A").start();

        // B是第2个顾客，看到窗口被A占用只能去候客区排队，进入AQS队列，等待A办理完成释放锁后进行抢占
        new Thread(()->{
            reentrantLock.lock();
            try {
                log.info("-----come in B");
            }finally {
                reentrantLock.unlock();
            }
        },"B").start();

        // C是第3个顾客，看到窗口被A占用只能去候客区排队，进入AQS队列，等A办理完成尝试抢占，C前面是B，FIFO
        new Thread(()->{
            reentrantLock.lock();
            try {
                log.info("-----come in C");
            }finally {
                reentrantLock.unlock();
            }
        },"C").start();

    }
}

