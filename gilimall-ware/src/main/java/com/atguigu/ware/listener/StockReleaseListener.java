package com.atguigu.ware.listener;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.constant.WareConstant;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.OrderVo;
import com.atguigu.common.vo.mq.StockLockedVo;
import com.atguigu.ware.dao.WareSkuDao;
import com.atguigu.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.ware.entity.WareOrderTaskEntity;
import com.atguigu.ware.enumration.LockStatusEnum;
import com.atguigu.ware.enumration.OrderStatusEnum;
import com.atguigu.ware.feign.OrderFeignService;
import com.atguigu.ware.service.WareOrderTaskDetailService;
import com.atguigu.ware.service.WareOrderTaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/09/18
 * @Description 解锁库存
 **/
@Slf4j
@Service
@RabbitListener(queues = WareConstant.STOCK_RELEASE_STOCK_QUEUE) // 监听"stock.release.stock.queue"队列
public class StockReleaseListener {
    @Autowired
    WareOrderTaskService wareOrderTaskService;
    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    OrderFeignService orderFeignService;
    @Autowired
    WareSkuDao wareSkuDao;

    /**
     * @description: 监听队列：解锁库存
     * 注意：解锁库存失败不要删消息，需要重新解锁 （手动ACK）
     **/
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedVo stockLockedVo, Message message, Channel channel) throws IOException {
        log.info("收到解锁库存的消息:{}", JSON.toJSONString(stockLockedVo));
        Long id=stockLockedVo.getId(); // 库存工作单id
        Long detailId=stockLockedVo.getDetail().getId(); // 库存工作单详情Id

        /**
         * 情况1： detailId在数据库wms_ware_order_task_detail表中不存在：库存本身锁定失败，自己回滚，无需解锁
         **/

        /**
         * 情况2： detailId在数据库wms_ware_order_task_detail表中存在,且状态为已锁定（解锁过的不重复解锁）：库存本身锁定成功，此时需要根据订单情况判断是否需要解锁
         *      1) oms_order表中无该订单： 订单出问题自己回滚了，要解锁库存
         *      2) oms_order表中有该订单： 订单状态为已取消，要解锁库存；其他情况表明一切正常，无需解锁。
         **/
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity=wareOrderTaskDetailService.getById(detailId);
        if(wareOrderTaskDetailEntity!=null&&wareOrderTaskDetailEntity.getLockStatus()==LockStatusEnum.LOCKED.getCode()){
            WareOrderTaskEntity wareOrderTaskEntity=wareOrderTaskService.getById(id);
            String orderSn=wareOrderTaskEntity.getOrderSn();
            Result<OrderVo> r= orderFeignService.getOrderStatus(orderSn);
            if(r.getCode()==0){
                OrderVo orderVo=r.getData();
                // oms_order表中无该订单或订单状态为已关闭(取消)  -> 解锁库存
                if(orderVo==null||orderVo.getStatus()==OrderStatusEnum.CLOSED.getCode()){
                    unLockStock(wareOrderTaskDetailEntity.getSkuId(),wareOrderTaskDetailEntity.getWareId(),wareOrderTaskDetailEntity.getSkuNum(),detailId);
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); // 手动ACK：解锁库存失败不会删消息
                }
            }else{
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true); //拒绝并将消息重新投放到队列
                log.error("远程查询订单状态失败,orderSn={}",orderSn);
            }
        }else{ // 无需解锁：直接把消息删掉
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }

    /**
     * @description: 防止订单服务卡顿（宕机）、消息积压等原因，
     * 库存解锁消息处理早于订单关闭（此时订单为新建状态，库存服务收到消息后啥也不做就走了，
     * 由于库存解锁消息被移除，后面即使订单关闭成功，库存也永远无法解锁）
     *     此处监听订单关闭后发来的消息（消息服务关单后主动发）
     * @param:
     * @param orderVo 同orderEntity
     * @param message
     * @param channel
     * @return: void
     **/
    @RabbitHandler
    public void handleOrderCloseRelease(OrderVo orderVo,Message message, Channel channel) throws IOException {
        log.info("收到订单关闭消息,准备解锁库存:{}",JSON.toJSONString(orderVo));
        try {
            String orderSn=orderVo.getOrderSn();
            // 根据订单号查库存工作单
            WareOrderTaskEntity wareOrderTaskEntity=wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
            // 查库存工作单详情，找到未解锁的 ，防止重复解锁
            List<WareOrderTaskDetailEntity> unlocked =wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id",wareOrderTaskEntity.getId()).eq("lock_status",LockStatusEnum.LOCKED.getCode()));
            unLockStockBatch(unlocked);   // 幂等：不用担心消重复发，只解锁库存工作单状态为锁定的库存（解锁后就将状态改为已解锁，后续无需解锁）
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);  // 解锁成功从队列移除
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true); //解锁出现异常将消息重新投放到队列
        }
    }

    @Transactional  // 批量解锁库存： 任一解锁失败 回滚
    public void unLockStockBatch(List<WareOrderTaskDetailEntity> unlocked){
        if(unlocked!=null&&!unlocked.isEmpty()){
            for(WareOrderTaskDetailEntity entity:unlocked){
                unLockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
            }
        }
    }

    /**
     * @description: 解锁库存
     * @param skuId
     * @param wareId 仓库id
     * @param num 解锁数量
     * @param detailId 库存工作详情单id
     * @return: void
     **/
    private void unLockStock(Long skuId,Long wareId,Integer num,Long detailId){
        wareSkuDao.unLockStock(skuId,wareId,num);

        // 更新库存工作详情单的状态
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity=new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(detailId);
        wareOrderTaskDetailEntity.setLockStatus(LockStatusEnum.UNLOCKED.getCode()); // 变为已解锁
        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);

    }

}
