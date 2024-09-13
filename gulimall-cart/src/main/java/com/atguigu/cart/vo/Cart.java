package com.atguigu.cart.vo;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 购物车
 **/
@Setter
public class Cart {
    /**
     * 购物项
     **/
    List<CartItem> items;
    /**
     * 商品数量
     **/
    private Integer countNum;
    /**
     * 商品类型数量
     **/
    private Integer countType;
    /**
     * 商品总价
     **/
    private BigDecimal totalPrice;
    /**
     * 减免价格
     **/
    private BigDecimal reduce=new BigDecimal(0);
    public List<CartItem> getItems() {
        return items;
    }
    public Integer getCountNum() {
        int count=0;
        if(items!=null&&!items.isEmpty()){
            for(CartItem cartItem:items){
                count +=cartItem.getCount();
            }
        }
        return count;
    }
    public Integer getCountType() {
        int count=0;
        if(items!=null&&!items.isEmpty()){
            count=items.size();
        }
        return count;
    }
    public BigDecimal getTotalPrice() {
        BigDecimal total=new BigDecimal(0);
        // 购物项总价
        if(items!=null&&!items.isEmpty()){
            for(CartItem cartItem:items){
                if(cartItem.getCheck()){
                    total=total.add(cartItem.getTotalPrice());
                }
            }
        }

        // 购物项总价-优惠总价
        total=total.subtract(getReduce());
        return total;
    }
    public BigDecimal getReduce() {
        return reduce;
    }
}
