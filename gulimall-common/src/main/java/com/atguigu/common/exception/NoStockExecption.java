package com.atguigu.common.exception;
import lombok.Data;
/**
 * @Author qiang.long
 * @Date 2024/09/16
 * @Description 没有库存异常
 **/
@Data
public class NoStockExecption extends RuntimeException{
    /**
     * 商品id
     **/
    private Long skuId;
    public NoStockExecption(){
        super("商品库存锁定失败");
    }
    public NoStockExecption(Long skuId){
        super("商品skuId="+skuId+"没有库存");
    }
}
