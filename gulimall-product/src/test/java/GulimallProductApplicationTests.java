import com.alibaba.fastjson.JSON;
import com.atguigu.product.GulimallProductApplication;
import com.atguigu.product.entity.BrandEntity;
import com.atguigu.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
/**
 * @Author qiang.long
 * @Date 2024/03/18
 * @Description 单元测试
 **/

@Slf4j
@SpringBootTest(classes = {GulimallProductApplication.class})
public class GulimallProductApplicationTests {
    @Resource
    private BrandService brandService;
    @Test
    public void insert(){
        BrandEntity brandEntity=new BrandEntity();
        brandEntity.setName("华为");
        brandEntity.setLogo("HUAWEI");
        brandService.save(brandEntity);
        log.info("保存成功");

    }
    @Test
    public void update(){
        BrandEntity brandEntity=new BrandEntity();
        brandEntity.setName("华为");
        brandEntity.setLogo("HUAWEI");
        brandEntity.setBrandId(13L);
        brandEntity.setDescript("测试修改");
        brandEntity.setFirstLetter("x");
        //brandService.updateById(brandEntity); // 按照id修改 （设置id、传brandEntity）
        brandService.update(brandEntity,new UpdateWrapper<BrandEntity>().eq("logo","HUAWEI"));
        log.info("修改成功");
    }
    @Test
    public void select(){
        /*
        List<BrandEntity> brandEntityList=brandService.list();
        brandEntityList.forEach(System.out::println);
         */
        BrandEntity brandEntity=brandService.list(new QueryWrapper<BrandEntity>().eq("logo","HUAWEI")).stream().findAny().get();
        log.info(JSON.toJSONString(brandEntity));



    }
}


