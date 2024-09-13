package com.atguigu.cart.service;
import com.atguigu.cart.vo.Cart;
import com.atguigu.cart.vo.CartItem;
/**
 * @Author qiang.long
 * @Date 2024/09/13
 * @Description
 **/

public interface CartService {
    /**
     * @description: 将商品加入购物车
     * @param:
     * @param skuId
     * @param num 数量
     * @return: CartItem
     **/
    CartItem addToCart(Long skuId, Integer num) throws Exception;
    CartItem getCartItem(Long skuId);
    /**
     * 获取购物车详情
     **/
    Cart getCart() throws Exception;
    void clearCart(String cartKey);
    /**
     * 修改购物车中购物项的选中状态
     **/
    void changeChecked(Long skuId, Integer check);
    /**
     * 修改购物车中购物项的数量
     **/
    void changeCount(Long skuId, Integer num);
}
