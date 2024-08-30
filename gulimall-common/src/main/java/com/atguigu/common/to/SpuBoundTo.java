package com.atguigu.common.to;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @Author qiang.long
 * @Date 2024/08/31
 * @Description
 **/
@Data
public class SpuBoundTo {
    /**
     * spu的ID
     **/
    private Long spuId;
    /**
     * 积分信息
     **/
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
