import com.atguigu.product.GulimallProductApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
/**
 * @Author qiang.long
 * @Date 2024/03/25
 * @Description
 **/

@Slf4j
@SpringBootTest(classes = GulimallProductApplication.class )
public class GulimallProductApplicationTest {

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


}
