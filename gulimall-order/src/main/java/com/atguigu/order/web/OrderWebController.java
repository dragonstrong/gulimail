package com.atguigu.order.web;
import com.alibaba.fastjson.JSON;
import com.atguigu.order.enumration.OrderSubmitErrorEnum;
import com.atguigu.order.service.OrderService;
import com.atguigu.order.vo.OrderConfirmVo;
import com.atguigu.order.vo.OrderSubmitVo;
import com.atguigu.order.vo.SubmitOrderRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;
/**
 * @Author qiang.long
 * @Date 2024/09/14
 * @Description
 **/
@Slf4j
@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;
    /**
     * @description: 去结算，生成订单:返回订单确认页数据
     **/
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo=orderService.confirm();
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return "confirm";
    }

    /**
     * @description: 提交订单
     * @param:
     * @param orderSubmitVo
     * @return: java.lang.String
     **/
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes){
        log.info("提交订单,{}",JSON.toJSONString(orderSubmitVo));
        // 创建订单，验令牌，验价格，锁库存

        // 下单成功跳到支付页

        // 下单失败回到订单确认页，重新确认订单信息
        SubmitOrderRespVo submitOrderRespVo=orderService.submitOrder(orderSubmitVo);
        log.info("订单提交的数据：{}", JSON.toJSONString(orderSubmitVo));
        if(submitOrderRespVo.getCode()==0){ // 下单成功：到支付页
            model.addAttribute("submitOrderResp",submitOrderRespVo);
            return "pay";
        }else{
            Integer errorCode=submitOrderRespVo.getCode();
            String errorMsg="下单失败，"+OrderSubmitErrorEnum.getMsgByCode(errorCode);
            redirectAttributes.addFlashAttribute("msg",errorMsg); // 给前端提示错误消息
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }
}
