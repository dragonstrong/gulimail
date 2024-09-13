import com.atguigu.product.GulimallProductApplication;
import com.atguigu.product.dao.AttrGroupDao;
import com.atguigu.product.dao.SkuSaleAttrValueDao;
import com.atguigu.product.entity.CategoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
/**
 * @Author qiang.long
 * @Date 2024/03/25
 * @Description
 **/

@Slf4j
@SpringBootTest(classes = GulimallProductApplication.class )
public class GulimallProductApplicationTest {
    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    /**
     * @description:创建交换机
     */
    //@Test
    public void createExchange(){
        /**DirectExchange全参构造
         DirectExchange(String name【交换机名字】,
         boolean durable【是否持久化】,
         boolean autoDelete【是否自动删除】,
         Map<String, Object> arguments【其他参数，对应前端arguments】)
         **/
        DirectExchange directExchange=new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange【{}】 创建成功",directExchange.getName());
    }

    /**
     * @description:创建队列
     */
    //@Test
    public void createQueue(){
        /**Queue全参构造
         Queue(String name【队列名】,
         boolean durable【是否持久化】,
         boolean exclusive【是否排它，即只能让当前连接连接到该队列，一般设为false】,
         boolean autoDelete【是否自动删除】,
         @Nullable Map<String, Object> arguments【其他参数，对应前端arguments】)
         **/
        amqpAdmin.declareQueue(new Queue("hello-java-queue", true,false,false));
        log.info("Queue【{}】 创建成功");
    }


    /**
     * @description:创建绑定
     */
    //@Test
    public void createBinding(){
        /**Binding全参构造
         Binding(String destination【目的地，传队列或交换机的名字】,
         DestinationType destinationType【类型，和队列还是交换机绑定】,
         String exchange【起始交换机】,
         String routingKey【路由键】,
         @Nullable Map<String, Object> arguments【自定义参数】)
         **/
        amqpAdmin.declareBinding(new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange","hello-java",null));
        log.info("Binding 创建成功");
    }


    /**
     * @description:发送消息
     */
    //@Test
    public void sendMessage(){
        /**convertAndSend参数
         convertAndSend(String exchange【给哪个交换机发消息】,
         String routingKey【路由键】,
         Object object【消息内容】)
         **/
        //rabbitTemplate.convertAndSend("hello-java-exchange","hello-java","hello-world!");
        // 以json形式发送消息对象
        CategoryEntity category=new CategoryEntity();
        category.setCatId(1L);
        category.setName("图书");
        category.setProductUnit("本");
        rabbitTemplate.convertAndSend("hello-java-exchange","hello-java",category);
        log.info("Message发送成功");
    }

    /*
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void testRedis(){

        ListOperations<String,String> ops=stringRedisTemplate.opsForList();
        ops.leftPushAll("list1","1","2","3","4");  // 左插 ["4","3","2","1"]
        List<String> list1=ops.range("list1",0,-1); // 获取
        log.info("list:{}",JSON.toJSONString(list1));
        ops.leftPop("list1");  // 最左边弹出一个元素  -> ["3","2","1"]
        ops.set("list1",1,"ab");  // 设置下标为1的元素  ["3","ab","1"]

        ops.remove("list1",2,"1"); // 移除2个1  -> ["3","ab"]
    }

    @Autowired
    private RedissonClient redissonClient;


    // 测试redisson
    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }


     */

    /*@Test
    public void testJoin(){
        List<SpuItemAttrGroupVo> attrGroupVos= attrGroupDao.getAttrGroupWithAttrs(19L,225L);
        log.info("attrGroupVos:{}",JSON.toJSONString(attrGroupVos));
        List<SkuItemSaleAttrVo> saleAttrs=skuSaleAttrValueDao.getSaleAttrs(19L);
        log.info("saleAttrs:{}",JSON.toJSONString(saleAttrs));
    }
     */


}
