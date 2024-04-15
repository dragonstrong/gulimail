package com.atguigu.product.thread;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
/**
 * @Author qiang.long
 * @Date 2024/04/07
 * @Description 统筹 线程
 **/
@Slf4j
public class TestThread {
    public static void main(String[] args) throws Exception {
       // 模拟转账
        Account a=new Account(1000);
        Account b=new Account(1000);

        Thread t1=new Thread(()->{
            for(int i=0;i<1000;i++){ // 转账1000次
                a.transfer(b,new Random().nextInt(5)+1);
            }
        },"t1");

        Thread t2=new Thread(()->{
            for(int i=0;i<1000;i++){ // 转账1000次
                b.transfer(a,new Random().nextInt(100)+1);
            }
        },"t2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.info("转账后账户a+账户b总金额：{}",a.getMoney()+b.getMoney());
    }
}
@AllArgsConstructor
@Data
class Account {
    private int money;
    public void transfer(Account target,int amount) {  // 模拟转账
        synchronized (Account.class){
            if (money>=amount){
                setMoney(money-amount);
                target.setMoney(target.getMoney()+amount);
            }
        }
    }
}