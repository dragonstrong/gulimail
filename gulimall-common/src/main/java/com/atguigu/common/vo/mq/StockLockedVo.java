package com.atguigu.common.vo.mq;
import lombok.Data;
/**
 * @Author qiang.long
 * @Date 2024/09/18
 * @Description sku库存锁定成功给MQ发送的消息
 **/
@Data
public class StockLockedVo {
    /**
     * 库存工作单id：一个id对应多个detailId
     **/
    private Long id;
    /**
     * 库存工作详情单
     **/
    private StockLockedDetailVo detail;

}
