package com.atguigu.order.service.impl;
import com.atguigu.common.constant.OrderConstant;
import com.atguigu.common.exception.NoStockExecption;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.Result;
import com.atguigu.common.vo.*;
import com.atguigu.order.dao.OrderDao;
import com.atguigu.order.entity.OrderEntity;
import com.atguigu.order.entity.OrderItemEntity;
import com.atguigu.order.enumration.OrderStatusEnum;
import com.atguigu.order.enumration.OrderSubmitErrorEnum;
import com.atguigu.order.feign.CartFeignService;
import com.atguigu.order.feign.MemberFeignService;
import com.atguigu.order.feign.ProductFeignService;
import com.atguigu.order.feign.WareFeignService;
import com.atguigu.order.interceptor.LoginUserInterceptor;
import com.atguigu.order.service.OrderItemService;
import com.atguigu.order.service.OrderService;
import com.atguigu.order.vo.OrderConfirmVo;
import com.atguigu.order.vo.OrderCreateVo;
import com.atguigu.order.vo.OrderSubmitVo;
import com.atguigu.order.vo.SubmitOrderRespVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    private ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal=new ThreadLocal<>();

    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }
    @Override
    public OrderConfirmVo confirm() throws ExecutionException, InterruptedException {
        MemberResponseVo member=LoginUserInterceptor.loginUser.get();
        OrderConfirmVo confirmVo=new OrderConfirmVo();
        //log.info("主线程...{}",Thread.currentThread().getId());

        RequestAttributes attributes= RequestContextHolder.getRequestAttributes(); // 给子线程传值
        CompletableFuture<Void> addressFuture=CompletableFuture.runAsync(()->{
            //log.info("member线程...{}",Thread.currentThread().getId());
            // 任务1. 远程查询用户地址列表 （gulimall-member）
            Result<List<MemberAddressVo>> r= memberFeignService.getAddressListByMemberId(member.getId());
            if(r.getCode()==0){
                confirmVo.setAddress(r.getData());
            }else{
                log.error("远程调用gulimall-member服务查询用户地址列表失败,memberId:{}",member.getId());
            }
        },threadPoolExecutor);

        CompletableFuture<Void> itemsFuture=CompletableFuture.runAsync(()->{
            RequestContextHolder.setRequestAttributes(attributes);// 获取父线程的RequestAttributes再设置自己的
            //log.info("cart线程...{}",Thread.currentThread().getId());
            // 任务2. 远程查询当前用户购物车中选中的数据 (gulimall-cart),价格为最新价格
            Result<List<OrderItemVo>> itemResult=cartFeignService.getCurrentUserCheckedCartItems();
            if(itemResult.getCode()==0){
                confirmVo.setItems(itemResult.getData());
            }else{
                log.error("远程调用gulimall-cart服务查询购物项列表失败,memberId:{}",member.getId());
            }
        },threadPoolExecutor).thenRunAsync(()->{
            // 任务2执行完后： 统一查询所有商品是否有货
            List<Long> skuIds=confirmVo.getItems().stream().map(item->item.getSkuId()).collect(Collectors.toList());
            Result<List<SkuHasStockVo>> stockResult=wareFeignService.getSkuHasStock(skuIds);
            if(stockResult.getCode()==0){
                Map<Long,Boolean> stockMap=stockResult.getData().stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId,SkuHasStockVo::getHasStock));
                confirmVo.setStocks(stockMap);
            }else{
                log.error("远程调用gulimall-cart服务查询购物项列表失败,memberId:{}",member.getId());
            }
        });

        CompletableFuture.allOf(addressFuture,itemsFuture).get();

        // 3. 查询用户积分
        confirmVo.setIntegration(member.getIntegration());

        // 4. 总价和应付自动计算

        // 5. TODO 防重令牌 幂等性（防止不断提交订单）
        String token= UUID.randomUUID().toString().replace("-","");
        confirmVo.setOrderToken(token); // 给页面（订单确认页）
        // 服务器存入redis供之后检验 （key-> order:token:userId）
        String redisOrderKey=OrderConstant.USER_ORDER_TOKEN_PREFIX+member.getId();
        stringRedisTemplate.opsForValue().set(redisOrderKey,token,30, TimeUnit.MINUTES); // 30分钟内检验令牌有效
        return confirmVo;
    }
    //@GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderRespVo submitOrder(OrderSubmitVo orderSubmitVo) {
        orderSubmitVoThreadLocal.set(orderSubmitVo);
        SubmitOrderRespVo submitOrderRespVo=new SubmitOrderRespVo();
        submitOrderRespVo.setCode(OrderSubmitErrorEnum.SUCCESS.getCode());
        MemberResponseVo member=LoginUserInterceptor.loginUser.get();
        // 1.验令牌【验证令牌和删令牌必须做成原子操作，防止并发出问题】
        // lua脚本: 执行后返回0失败  1成功
        String script="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken=orderSubmitVo.getOrderToken(); //前端传来的令牌
        Long result=stringRedisTemplate.execute(new DefaultRedisScript<Long>(script,Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX+member.getId()),orderToken);
        if(result==0L){ // 令牌检验失败
            submitOrderRespVo.setCode(OrderSubmitErrorEnum.TOKEN_INVALID.getCode());
            return submitOrderRespVo;
        }else{ // 令牌校验成功
            // 2.创建订单、订单项等信息
            OrderCreateVo orderCreateVo=createOrder();
            // 3.验价
            BigDecimal payAmount=orderCreateVo.getOrder().getPayAmount();
            BigDecimal payPrice=orderSubmitVo.getPayPrice(); // 页面提交过来的应付金额
            if(Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){ // 对比成功
                // 4. 保存订单、订单项到数据库
                saveOrder(orderCreateVo);
                // 5. 远程锁定库存: 只要有异常，回滚订单数据
                WareSkuLockVo wareSkuLockVo=new WareSkuLockVo();
                wareSkuLockVo.setOrderSn(orderCreateVo.getOrder().getOrderSn());
                List<OrderItemVo> locks=orderCreateVo.getOrderItems().stream().map(orderItemEntity->{
                    OrderItemVo orderItemVo=new OrderItemVo();
                    orderItemVo.setSkuId(orderItemEntity.getSkuId());
                    orderItemVo.setTitle(orderItemEntity.getSkuName());
                    orderItemVo.setCount(orderItemEntity.getSkuQuantity());
                    return orderItemVo;
                }).collect(Collectors.toList());
                wareSkuLockVo.setLocks(locks);
                Result<Boolean> r=wareFeignService.orderLockStock(wareSkuLockVo);
                if(r.getCode()==0){ // 锁定成功
                    submitOrderRespVo.setOrder(orderCreateVo.getOrder());
                    // TODO 分布式事务：远程调用成功后发生异常 如何让远程事务
                    // int i=10/0;
                    // TODO 下单成功，发送消息给MQ（为了后面定时关单）
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderCreateVo.getOrder());
                    return submitOrderRespVo;
                }else{ // 锁定失败
                    log.error("远程调用gulimall-ware锁定库存失败");
                    submitOrderRespVo.setCode(OrderSubmitErrorEnum.STOCK_LOCK_FAIL.getCode());
                    throw new NoStockExecption();  // 不手动抛异常：库存锁定失败订单不回滚
                }
            }else{
                submitOrderRespVo.setCode(OrderSubmitErrorEnum.PRICE_CKECK_FAIL.getCode()); //验价失败
                log.error("检验订单价格失败，payAmount={}，payPrice={}",payAmount,payPrice);
                return submitOrderRespVo;
            }
        }
    }
    @Override
    public Result<OrderEntity> getOrderStatus(String orderSn) {
        OrderEntity order=getBaseMapper().selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        order.setModifyTime(null);
        return Result.ok(order);
    }
    /**
     * @description: 关闭订单
     * @param:
     * @param order
     * @return: void
     **/
    @Override
    public void closeOrder(OrderEntity order) {
        OrderEntity orderInDB=getBaseMapper().selectById(order.getId());
        if(orderInDB!=null&&orderInDB.getStatus()==OrderStatusEnum.NEW.getCode()){ // 订单在数据库里存在且状态为未支付（到队列已超时）
            // 更新状态
            OrderEntity update=new OrderEntity();
            update.setId(orderInDB.getId());
            update.setStatus(OrderStatusEnum.CLOSED.getCode());
            update.setModifyTime(new Date());
            updateById(update);
            // 消息积压、订单服务卡顿宕机等原因导致订单关闭消息未及时消费，
            // 库存解锁晚于订单关闭，订单状态是新建导致解锁库存失败，且消息被消费移除永久无法解锁库存
            // -->> 主动往"stock.release.stock.queue"发送消息订单已关闭，库存服务收到后立即解锁对应的库存
            OrderVo orderVo=new OrderVo();
            BeanUtils.copyProperties(orderInDB,orderVo);
            rabbitTemplate.convertAndSend("order-event-exchange","order.release.other",orderVo);
        }
    }
    /**
     * @description: 创建订单
     * @param:
     * @return: com.atguigu.order.vo.OrderCreateVo
     **/

    private OrderCreateVo createOrder() {
        OrderCreateVo orderCreateVo = new OrderCreateVo();
        String orderSn = IdWorker.getTimeId(); // 订单号
        // 1.构建订单
        OrderEntity orderEntity = buildOrderEntity(orderSn);
        orderCreateVo.setOrder(orderEntity);
        // 2.构建所有订单项
        List<OrderItemEntity> orderItems=buildOrderItems(orderSn);
        orderCreateVo.setOrderItems(orderItems);
        // 3.验价：计算价格相关
        compute(orderEntity,orderItems);

        return orderCreateVo;
    }

    private OrderEntity buildOrderEntity(String orderSn){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        MemberResponseVo member=LoginUserInterceptor.loginUser.get();
        orderEntity.setMemberId(member.getId());
        //获取收获地址信息
        OrderSubmitVo orderSubmitVo = orderSubmitVoThreadLocal.get();
        // 获取运费
        Result<FareVo> fareResult = wareFeignService.getFreight(orderSubmitVo.getAddrId());
        if (fareResult.getCode() == 0) {
            FareVo fareVo = fareResult.getData();
            // 运费
            orderEntity.setFreightAmount(fareVo.getFare());
            // 收货人信息
            orderEntity.setReceiverName(fareVo.getAddress().getName()); // 姓名
            orderEntity.setReceiverProvince(fareVo.getAddress().getProvince()); // 省
            orderEntity.setReceiverCity(fareVo.getAddress().getCity()); // 市
            orderEntity.setReceiverPhone(fareVo.getAddress().getPhone()); // 电话
            orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress()); // 详细地址
            orderEntity.setReceiverPostCode(fareVo.getAddress().getPostCode()); // 邮政编码
            orderEntity.setReceiverRegion(fareVo.getAddress().getRegion());

            // 设置订单状态
            orderEntity.setStatus(OrderStatusEnum.NEW.getCode());
            orderEntity.setDeleteStatus(0); // 0未删除

        } else {
            log.error("远程调用gulimall-ware获取运费信息失败,addrId={}", orderSubmitVo.getAddrId());
        }
        return orderEntity;
    }

    /**
     * 构建所有订单项
     **/
    private List<OrderItemEntity> buildOrderItems(String orderSn){
        // 2. 获取所有订单项
        Result<List<OrderItemVo>> cartItemsResult = cartFeignService.getCurrentUserCheckedCartItems();
        if (cartItemsResult.getCode() == 0) {
            List<OrderItemVo> cartItems = cartItemsResult.getData();
            if (cartItems != null) {
                List<OrderItemEntity> orderItemEntities=cartItems.stream().map(orderItemVo -> {
                    OrderItemEntity orderItemEntity=buildOrderItem(orderItemVo);
                    orderItemEntity.setOrderSn(orderSn);  // 订单号
                    return orderItemEntity;
                }).collect(Collectors.toList());
                return orderItemEntities;
            } else {
                return null;
            }
        } else {
            log.error("远程调用gulimall-cart获取订单项信息失败");
            return null;
        }
    }

    /**
     * 构建单个订单项
     **/
    private OrderItemEntity buildOrderItem(OrderItemVo orderItemVo){
        OrderItemEntity orderItemEntity=new OrderItemEntity();
        // 1.商品SPU信息
        Result<SpuInfoVo> spuResult=productFeignService.getSpuInfoBySkuId(orderItemVo.getSkuId());
        if(spuResult.getCode()==0){
            SpuInfoVo spuInfoVo=spuResult.getData();
            if(spuInfoVo!=null){
                orderItemEntity.setSpuId(spuInfoVo.getId());
                orderItemEntity.setSpuName(spuInfoVo.getSpuName());
                orderItemEntity.setSpuBrand(spuInfoVo.getBrandId().toString());
                orderItemEntity.setCategoryId(spuInfoVo.getCatalogId());
            }
        }else{
            log.error("远程调用gulimall-product获取spu信息失败");
        }

        // 2.商品sku信息
        orderItemEntity.setSkuId(orderItemVo.getSkuId());
        orderItemEntity.setSkuName(orderItemVo.getTitle());
        orderItemEntity.setSkuPrice(orderItemVo.getPrice());
        orderItemEntity.setSkuPic(orderItemVo.getImage());
        String skuAttr= StringUtils.collectionToDelimitedString(orderItemVo.getSkuAttr(),";");
        orderItemEntity.setSkuAttrsVals(skuAttr);
        orderItemEntity.setSkuQuantity(orderItemVo.getCount());
        // 3.优惠信息：不做

        // 4.积分信息
        orderItemEntity.setGiftGrowth(orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount())).intValue());
        orderItemEntity.setGiftIntegration(orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount())).intValue());

        // 5.订单项的价格信息
        orderItemEntity.setPromotionAmount(new BigDecimal("0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        BigDecimal realPrice=orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity())).
                subtract(orderItemEntity.getCouponAmount()).subtract(orderItemEntity.getPromotionAmount())
                        .subtract(orderItemEntity.getIntegrationAmount());   // 当前订单项实际金额
        orderItemEntity.setRealAmount(realPrice); // 当前订单项实际金额

        return orderItemEntity;
    }


    /**
     * 验价
     **/
    private void compute(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        BigDecimal total=new BigDecimal("0"); // 总金额
        BigDecimal coupon=new BigDecimal("0"); // 总优惠券
        BigDecimal integration=new BigDecimal("0"); // 总积分优惠
        BigDecimal promotion=new BigDecimal("0"); // 总促销优惠

        BigDecimal gift=new BigDecimal("0"); // 总积分
        BigDecimal growth=new BigDecimal("0"); // 总成长值

        for(OrderItemEntity item:orderItems){
            total=total.add(item.getRealAmount());
            coupon=coupon.add(item.getCouponAmount());
            integration=integration.add(item.getIntegrationAmount());
            promotion=promotion.add(item.getPromotionAmount());

            gift=gift.add(new BigDecimal(item.getGiftIntegration()));
            growth=growth.add(new BigDecimal(item.getGiftGrowth()));

        }
        orderEntity.setTotalAmount(total);
        orderEntity.setCouponAmount(coupon);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount())); // 应付=总+运费
    }

    /**
     * @description: 保存订单、订单项到数据库
     **/
    private void saveOrder(OrderCreateVo orderCreateVo) {
        OrderEntity orderEntity=orderCreateVo.getOrder();
        //orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(new Date());
        save(orderEntity);

        List<OrderItemEntity> itemEntities=orderCreateVo.getOrderItems();
        orderItemService.saveBatch(itemEntities);
    }



}
