package com.atguigu.common.vo;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @Author qiang.long
 * @Date 2024/09/15
 * @Description 收货地址+运费
 **/
@Data
public class FareVo {
    /**
     * 收获地址
     **/
    private MemberAddressVo address;
    /**
     * 运费
     **/
    private BigDecimal fare;

}
