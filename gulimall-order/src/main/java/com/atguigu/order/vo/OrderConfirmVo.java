package com.atguigu.order.vo;
import com.atguigu.common.vo.MemberAddressVo;
import com.atguigu.common.vo.OrderItemVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
/**
 * @Author qiang.long
 * @Date 2024/09/14
 * @Description 订单确认页数据
 **/


public class OrderConfirmVo {
    /**
     * 用户收获地址列表
     **/
    @Getter @Setter
    private List<MemberAddressVo> address;
    /**
     * 购物项
     **/
    @Getter @Setter
    private List<OrderItemVo> items;

    /**
     * 优惠券信息（积分）
     */
    @Getter @Setter
    private Integer integration;
    /**
     * 商品总数量
     */
    private Integer count;

    /**
     * 订单总额
     */
    private BigDecimal totalPrice;
    /**
     * 应付总额
     */
    private BigDecimal payPrice;
    /**
     * 是否有库存 key:skuId
     */
    @Getter @Setter
    private Map<Long,Boolean> stocks;


    /**
     * 防重令牌
     */
    @Getter @Setter
    private String orderToken;


    public BigDecimal getTotalPrice() {
        BigDecimal sum=new BigDecimal("0");
        if(items!=null){
            for(OrderItemVo itemVo:items){
                BigDecimal multiply=itemVo.getPrice().multiply(new BigDecimal(itemVo.getCount().toString()));
                sum=sum.add(multiply);
            }
        }
        return sum;
    }
    public BigDecimal getPayPrice() {
        return getTotalPrice();
    }
    /**
     * 计算商品数量
     */
    public Integer getCount() {
        Integer sum=0;
        if(items!=null){
            for(OrderItemVo itemVo:items){
                sum += itemVo.getCount();
            }
        }
        return sum;
    }
}
