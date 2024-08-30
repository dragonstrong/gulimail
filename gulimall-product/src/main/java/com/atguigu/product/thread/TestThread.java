package com.atguigu.product.thread;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
/**
 * @Author qiang.long
 * @Date 2024/04/07
 * @Description 统筹 线程
 **/
@Slf4j
public class TestThread {
    public static void main(String[] args) throws InterruptedException {
        //TestSync testSync=new TestSync();
        ArrayList<Integer> list=new ArrayList<>();
        Thread t1=new Thread(()->{
            list.add(1);
            log.info("t1---> size={}",list.size());
        },"t1");
        ReentrantLock reentrantLock=new ReentrantLock();
        reentrantLock.lock();


        LinkedList<Integer> list1=new LinkedList<>();
        list1.add(1);
        list1.add(2);
        list1.add(4);
        list1.add(2,3);
        log.info("list1:{}",list1);


        Thread t2=new Thread(()->{
            list.add(2);
            log.info("t2---> size={}",list.size());
        },"t2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.info("list={}",list);
        }
}

class TestFinal{
    static int A=10;
    final static int B=Short.MAX_VALUE;
    final int a=20;
    final int b=Integer.MAX_VALUE;
    final void test1(){
    }

}

class UseFinal{
    public void test(){
        System.out.println(TestFinal.A);
        System.out.println(TestFinal.B);
        System.out.println(new TestFinal().a);
        System.out.println(new TestFinal().b);
        new TestFinal().test1();
    }
}

class UseFinal2{
    public void test(){
        System.out.println(TestFinal.A);
    }
}

@Slf4j
class TestSync{
    @Transactional
    public synchronized void m1(){
        log.info("m1 running...");
    }

    public synchronized void m2(){
        log.info("m2 running...");
    }


}
@Data
class Test{
    public static Integer i;
    private Integer j;
    public Test(Integer i,Integer j){
        this.i=i;
        this.j=j;
    }
    public Test(Integer j) {
        this.j = j;
    }
    public void setI(Integer i){
        this.i=i;
    }
}