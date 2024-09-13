package com.atguigu.cart.service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.cart.feign.ProductFeignService;
import com.atguigu.cart.interceptor.CartInterceptor;
import com.atguigu.cart.vo.Cart;
import com.atguigu.cart.vo.CartItem;
import com.atguigu.cart.vo.UserInfoVo;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.utils.R;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
/**
 * @Author qiang.long
 * @Date 2024/09/13
 * @Description
 **/

@Slf4j
@Service
public class CartServiceImpl implements CartService{
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Override
    public CartItem addToCart(Long skuId, Integer num) throws Exception {
        BoundHashOperations<String,Object,Object> ops=getCartOps();
        Object o=ops.get(skuId.toString());
        if(o==null){ // 购物车中无此商品
            CartItem cartItem = new CartItem();
            // 任务1： 远程获取sku详情
            CompletableFuture<Void> skuInfoFuture=CompletableFuture.runAsync(()->{
                R r=productFeignService.skuInfo(skuId);
                if(r.getCode()==0) {
                    SkuInfoVo skuInfoVo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    cartItem.setSkuId(skuId);
                    cartItem.setCheck(true);
                    cartItem.setCount(num);
                    cartItem.setImage(skuInfoVo.getSkuDefaultImg());
                    cartItem.setTitle(skuInfoVo.getSkuTitle());
                    cartItem.setPrice(skuInfoVo.getPrice());
                }else {
                    log.error("远程调用gulimall-product查询sku详情失败，skuId={}",skuId);
                }
            },threadPoolExecutor);

            // 任务2.远程查询sku的销售组合信息
            CompletableFuture<Void> skuSaleAttrCombineFuture=CompletableFuture.runAsync(()->{
                Result<List<String>> r=productFeignService.getSkuSaleAttrsCombine(skuId);
                if(r.getCode()==0) {
                    cartItem.setSkuAttr(r.getData());
                }else {
                    log.error("远程调用gulimall-product查询sku销售属性组合失败，skuId={}", skuId);
                }

            },threadPoolExecutor);
            CompletableFuture.allOf(skuInfoFuture,skuSaleAttrCombineFuture).get();

            // 3.将<skuId,cartItem>键值对存入redis
            ops.put(skuId.toString(), JSON.toJSONString(cartItem));
            //log.info("添加商品到购物车:{}",JSON.toJSONString(cartItem));
            return cartItem;
        }else{ // 购物车中有该商品，直接改数量
            CartItem cartItemRedis=JSON.parseObject((String) o,CartItem.class);
            cartItemRedis.setCount(cartItemRedis.getCount()+num);
            ops.put(skuId.toString(), JSON.toJSONString(cartItemRedis));
            return cartItemRedis;
        }
    }
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String,Object,Object> ops=getCartOps();
        Object o=ops.get(skuId.toString());
        if(o!=null){
            return JSON.parseObject((String) o,CartItem.class);
        }else{
            log.error("redis中没有skuId={}的商品信息",skuId);
            return null;
        }
    }
    @Override
    public Cart getCart() throws Exception {
        Cart cart=new Cart();
        UserInfoVo userInfoVo= CartInterceptor.threadLocal.get();
        if(userInfoVo.getUserId()!=null){ // 已登录：用户购物车  且要考虑从游客购物车到用户购物车的切换（刚登录需要合并游客购物车中的商品）
            // 1. 判断临时购物车中有无数据：判断是否从游客切换到登录
            String cartKeyInTour= CartConstant.CART_OREFIX+userInfoVo.getUserKey();
            List<CartItem> cartItemsInTour=getCartItems(cartKeyInTour);
            // 2. 游客购物车有商品：合并到用户购物车
            if(cartItemsInTour!=null){
                for(CartItem cartItem:cartItemsInTour){
                    addToCart(cartItem.getSkuId(),cartItem.getCount());
                }
            }
            // 3. 获取用户购物车
            String cartKeyInLogin= CartConstant.CART_OREFIX+userInfoVo.getUserId();
            List<CartItem> cartItemsInLogin=getCartItems(cartKeyInLogin);
            cart.setItems(cartItemsInLogin);
            // 3. 清除临时购物车
            clearCart(cartKeyInTour);
        }else{ // 未登录：游客购物车
            String cartKey= CartConstant.CART_OREFIX+userInfoVo.getUserKey();
            cart.setItems(getCartItems(cartKey));
        }
        return cart;
    }
    /**
     * 获取购物车的hash操作
     **/
    private BoundHashOperations<String,Object,Object> getCartOps(){
        UserInfoVo userInfoVo =CartInterceptor.threadLocal.get();
        String cartKey="";
        if(userInfoVo.getUserId()!=null){ // 已登录：使用用户购物车
            cartKey=CartConstant.CART_OREFIX+userInfoVo.getUserId();
        }else{ // 未登录：使用临时（游客）购物车
            cartKey=CartConstant.CART_OREFIX+userInfoVo.getUserKey();
        }
        BoundHashOperations<String,Object,Object> ops=redisTemplate.boundHashOps(cartKey);
        return ops;
    }
    /**
     * @description: 获取购物车中的所有购物项
     * @param:
     * @param cartKey
     * @return: java.util.List<com.atguigu.cart.vo.CartItem>
     **/
    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String,Object,Object> ops=redisTemplate.boundHashOps(cartKey);
        List<Object> values=ops.values();
        if(values!=null){
            List<CartItem> cartItems=values.stream().map(o->{
                CartItem cartItem=JSON.parseObject((String) o,CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return cartItems;
        }
        return null;
    }

    /**
     * @description: 清空购物车
     * @param:
     * @param cartKey
     * @return: void
     **/
    @Override
    public void clearCart(String cartKey){
        redisTemplate.delete(cartKey);
    }
    @Override
    public void changeChecked(Long skuId, Integer check) {
        CartItem cartItem=getCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        getCartOps().put(skuId.toString(),JSON.toJSONString(cartItem));
    }
    @Override
    public void changeCount(Long skuId, Integer num) {
        CartItem cartItem=getCartItem(skuId);
        cartItem.setCount(num);
        getCartOps().put(skuId.toString(),JSON.toJSONString(cartItem));
    }
}
