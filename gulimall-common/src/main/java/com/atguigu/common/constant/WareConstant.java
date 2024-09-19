package com.atguigu.common.constant;
/**
 * @Author qiang.long
 * @Date 2024/09/18
 * @Description 库存服务常量
 **/
public class WareConstant {
    /**
     * 死信队列名
     **/
    public static final String STOCK_DELAY_QUEUE="stock.delay.queue";
    /**
     * stock.release.stock.queue 队列名
     **/
    public static final String STOCK_RELEASE_STOCK_QUEUE="stock.release.stock.queue";
    /**
     * stock-event-exchange 交换机
     **/
    public static final String STOCK_EVENT_EXCHANGE="stock-event-exchange";

    /**
     * STOCK_LOCKED_ROUTING_KEY
     **/
    public static final String STOCK_LOCKED_ROUTING_KEY="stock.locked";
    /**
     * STOCK_RELEASE_ROUTING_KEY
     **/
    public static final String STOCK_RELEASE_ROUTING_KEY="stock.release.#";


}
