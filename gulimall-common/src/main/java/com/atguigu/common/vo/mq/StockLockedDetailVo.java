package com.atguigu.common.vo.mq;
import lombok.Data;
/**
 * @Author qiang.long
 * @Date 2024/09/18
 * @Description
 **/
@Data
public class StockLockedDetailVo {
    /**
     * id
     */
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 锁定状态： 1-已锁定  2-已解锁  3-扣减
     */
    private Integer lockStatus;
}
