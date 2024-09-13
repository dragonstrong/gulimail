package com.atguigu.cart.controller;
import com.atguigu.cart.service.CartService;
import com.atguigu.cart.vo.Cart;
import com.atguigu.cart.vo.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * @Author qiang.long
 * @Date 2024/09/13
 * @Description
 **/
@Slf4j
@Controller
public class CartController {
    @Autowired
    CartService cartService;

    /**
     * @description: 跳转到购物车列表页
     *      浏览器有一个cookie： 用user-key标识用户身份，一个月过期
     *      如果第一次使用jd的购物车，会给一个临时身份
     *      浏览器以后保存，每次访问都带上这个cookie
     *
     *      登录了：session有
     *      没登陆：按照cookie里带来的user-key
     *
     *
     * @param:
     * @return: java.lang.String
     **/
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws Exception {
        Cart cart=cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * @description: 添加商品到购物车
     * @param:  RedirectAttributes.addAttribute()： 将数据放在url后面
     *          RedirectAttributes.addFlashAttribute()：将数据放在session里可在页面取出，但只能取一次
     * @param skuId 商品id(sku)
     * @param num 数量
     * @return: String
     **/
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes) throws Exception {
        log.info("添加商品到购物车：skuId={},num={}",skuId,num);
        cartService.addToCart(skuId,num);
        // 添加到购物车 http://cart.gulimall.com/addToCartSuccessPage.html?skuId=48&num=1
        // 跳转到http://cart.gulimall.com/addToCartSuccessPage.html?skuId=48
        // 而非http://cart.gulimall.com/addToCartSuccessPage.html?skuId=48&num=1 ，可防止重刷
        redirectAttributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html"; // 重定向到addToCartSuccessPage.html
    }

    /**
     * 重定向到成功页面，再查一遍cartItem, 防止重复添加
     **/
    @GetMapping("/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId")Long skuId, Model model){
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";  // 跳到success.html
    }
    /**
     * @description: 修改购物车中商品选中状态
     * @param: 
     * @param skuId
     * @param check 1被选中 0 未选中
     * @return: java.lang.String
     **/
    @GetMapping("/checkItem.html")
    public String checkItem(@RequestParam("skuId") Long skuId,@RequestParam("check") Integer check){
        cartService.changeChecked(skuId,check);
        return "redirect:http://cart.gulimall.com/cart.html"; // 重定向到购物车列表页
    }


    /**
     * @description: 修改购物车中商品的数量
     * @param:
     * @param skuId
     * @param num
     * @return: java.lang.String
     **/
    @GetMapping("/countItem.html")
    public String countItem(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){
        cartService.changeCount(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html"; // 重定向到购物车列表页
    }



}
