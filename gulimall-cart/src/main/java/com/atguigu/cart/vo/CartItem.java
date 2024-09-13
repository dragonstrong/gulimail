package com.atguigu.cart.vo;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/11
 * @Description 购物车中的单个购物项
 **/
@Setter
public class CartItem {
    /**
     * 商品sku id
     **/
    private Long skuId;
    /**
     * 购物项是否被选中
     **/
    private Boolean check=true;
    /**
     * 商品标题
     **/
    private String title;
    /**
     * 商品图片
     **/
    private String image;
    /**
     * 商品套餐信息：可能买了很多sku(销售组合)
     **/
    private List<String> skuAttr;
    /**
     * 价格
     **/
    private BigDecimal price;
    /**
     * 数量
     **/
    private Integer count;
    /**
     * 总价格
     **/
    private BigDecimal totalPrice;
    public Long getSkuId() {
        return skuId;
    }
    public Boolean getCheck() {
        return check;
    }
    public String getTitle() {
        return title;
    }
    public String getImage() {
        return image;
    }
    public List<String> getSkuAttr() {
        return skuAttr;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public Integer getCount() {
        return count;
    }
    public BigDecimal getTotalPrice() {
        this.totalPrice = price.multiply(new BigDecimal(count));
        return totalPrice;
    }
}
