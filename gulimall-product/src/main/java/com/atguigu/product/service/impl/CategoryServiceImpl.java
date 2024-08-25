package com.atguigu.product.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.product.dao.CategoryDao;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.service.CategoryService;
import com.atguigu.product.vo.Catalog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    private static final String CATALOG_JSON = "catalogJson";
    private static final String LOCK = "LOCK";
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );
        return new PageUtils(page);
    }
    /**
     * @description: 查出所有分类以及子分类，并以树形结构组装起来
     * @param:
     * @return: List<CategoryEntity>
     **/
    @Override
    public List<CategoryEntity> getCategoryTree() {
        List<CategoryEntity> categoryEntityList = list();
        // 找出1级分类并设置子菜单
        List<CategoryEntity> res = categoryEntityList.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0).map(category -> {
            category.setChildren(getChildren(category, categoryEntityList));
            return category;
        }).sorted((category1, category2) -> {
            return (category1.getSort() == null ? 0 : category1.getSort()) - (category2.getSort() == null ? 0 : category2.getSort());
        }).collect(Collectors.toList());
        return res;
    }
    /**
     * @param category          当前菜单
     * @param allCategoryEntity 所菜单
     * @description: 递归找子菜单
     * @param:
     * @return: List<CategoryEntity>
     **/
    public List<CategoryEntity> getChildren(CategoryEntity category, List<CategoryEntity> allCategoryEntity) {
        List<CategoryEntity> child = allCategoryEntity.stream().filter(category1 -> category1.getParentCid() == category.getCatId()).map(category2 -> {
            category2.setChildren(getChildren(category2, allCategoryEntity));
            return category2;
        }).sorted((muen1, muen2) -> {
            return (muen1.getSort() == null ? 0 : muen1.getSort()) - (muen2.getSort() == null ? 0 : muen2.getSort());
        }).collect(Collectors.toList());
        category.setChildren(child);
        return child;
    }
    /**
     * @description: p138 二级三级分类数据
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJsonByDB() {
        log.info("查询了数据库");
        Map<String, List<Catalog2Vo>> map = null;  // 结果
        // 找一级分类
        List<CategoryEntity> categoryEntityList = getBaseMapper().selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        if (categoryEntityList != null)
        {
            // 对每一个一级分类找二级分类
            // 封装成map   key=一级分类的catId  value=List<Catalog2Vo>
            map = categoryEntityList.stream().collect(Collectors.toMap(k -> {
                return k.getCatId().toString();
            }, v -> {
                // 找v的二级分类列表
                List<Catalog2Vo> catalog2VoList = null;
                List<CategoryEntity> category2Entities = getBaseMapper().selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
                if (category2Entities != null)
                {
                    // 将category2Entities封装成List<Catalog2Vo>
                    // 先找3级子分类列表
                    catalog2VoList = category2Entities.stream().map(l2 -> {
                        List<CategoryEntity> category3Entities = getBaseMapper().selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                        List<Catalog2Vo.Category3Vo> category3VoList = null;
                        if (category3Entities != null)
                        {
                            category3VoList = category3Entities.stream().map(l3 -> {
                                Catalog2Vo.Category3Vo catalog3Vo = new Catalog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                return catalog3Vo;
                            }).collect(Collectors.toList());
                        }
                        Catalog2Vo catalog2Vo = new Catalog2Vo(l2.getParentCid().toString(), category3VoList, l2.getCatId().toString(), l2.getName());
                        return catalog2Vo;
                    }).collect(Collectors.toList());
                }
                return catalog2VoList;
            }));
        }
        return map;
    }


    /**
     * @description: 加入SpringCache缓存注解
     * @Cacheable： 代表当前的结果需要缓存，如果缓存中有，方法不用调用，否则调用方法并将方法的结果放入缓存。{"getCatalogJsonByDBUseSpringCache"}指定缓存分区，是一个数组，可以放在多个分区
     * 自定义：
     * 1）指定key: key属性执行，接受一个SpEL,固定值加单引号。#root.methodName表示取方法名作为key。
     * SpEL表达式参考：https://docs.spring.io/spring-framework/reference/integration/cache/annotations.html
     * 2)指定过期时间：配置文件中 spring.cache.redis.time-to-live, 单位ms
     * 3）将数据保存为json格式： 默认使用jdk序列化。将数据保存为json格式涉及自定义缓存管理器。
     *
     * 自定义缓存管理器原理：
     * CacheAutoConfiguration ->导入RedisCacheConfiguration ->自动配置了RedisCacheManager -> 初始化所有的缓存 ->每个缓存决定使用什么配置
     * -> 如果RedisCacheConfiguration有就用已有的，没有就用默认配置
     * -> 想改缓存配置，只需给容器中放一个RedisCacheConfiguration即可
     * -> 就会应用到当前RedisCacheManager管理的所有缓存分区中。
     *
     * 指定序列化方式： keySerializationPair  valueSerializationPair
     **/
    @Cacheable(value = {"product"},key="#root.methodName")
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJsonByDBUseSpringCache() {
        log.info("查询了数据库");
        Map<String, List<Catalog2Vo>> map = null;  // 结果
        // 找一级分类
        List<CategoryEntity> categoryEntityList = getBaseMapper().selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        if (categoryEntityList != null)
        {
            // 对每一个一级分类找二级分类
            // 封装成map   key=一级分类的catId  value=List<Catalog2Vo>
            map = categoryEntityList.stream().collect(Collectors.toMap(k -> {
                return k.getCatId().toString();
            }, v -> {
                // 找v的二级分类列表
                List<Catalog2Vo> catalog2VoList = null;
                List<CategoryEntity> category2Entities = getBaseMapper().selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
                if (category2Entities != null)
                {
                    // 将category2Entities封装成List<Catalog2Vo>
                    // 先找3级子分类列表
                    catalog2VoList = category2Entities.stream().map(l2 -> {
                        List<CategoryEntity> category3Entities = getBaseMapper().selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                        List<Catalog2Vo.Category3Vo> category3VoList = null;
                        if (category3Entities != null)
                        {
                            category3VoList = category3Entities.stream().map(l3 -> {
                                Catalog2Vo.Category3Vo catalog3Vo = new Catalog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                return catalog3Vo;
                            }).collect(Collectors.toList());
                        }
                        Catalog2Vo catalog2Vo = new Catalog2Vo(l2.getParentCid().toString(), category3VoList, l2.getCatId().toString(), l2.getName());
                        return catalog2Vo;
                    }).collect(Collectors.toList());
                }
                return catalog2VoList;
            }));
        }
        return map;
    }


    /**
     * @description: p138 二级三级分类数据(使用redis)
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJsonUseRedisWithLocalLock() {
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            // 先查redis缓存，没有再从mysql封装
            String catalogJson = stringRedisTemplate.opsForValue().get(CATALOG_JSON);
            // 查数据库
            if (catalogJson == null) {
                // 不能让所有线程都去访问数据库，压力太大，加锁  一个线程访问了写入缓存，后面其他线程都从缓存拿就行
                synchronized (this){
                    // 再查一遍缓存 （并发）
                    ValueOperations<String, String> ops1 = stringRedisTemplate.opsForValue();
                    // 先查redis缓存，没有再从mysql封装
                    String catalogJson1 = stringRedisTemplate.opsForValue().get(CATALOG_JSON);
                    if (catalogJson1!=null){
                        return JSON.parseObject(catalogJson1, new TypeReference<Map<String, List<Catalog2Vo>>>() {
                        });
                    }
                    Map<String, List<Catalog2Vo>> map = getCatalogJsonByDB();
                    // 写入redis (先转为json String, 方便跨语言和平台，直接写入java数据结构其他语言获取不方便)
                    ops.set(CATALOG_JSON, JSON.toJSONString(map));
                    return map;
                }
            } else {
                // 缓存里有直接返回
                log.info("缓存中有二级三级分类数据，直接返回");
                Map<String, List<Catalog2Vo>> map = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
                });
                return map;
            }
    }
    /**
     * @description: p138 二级三级分类数据(使用redis) 加分布式锁
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJsonWithLock() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 先查redis缓存，没有再从mysql封装
        String catalogJson = stringRedisTemplate.opsForValue().get(CATALOG_JSON);
        // 查数据库
        Map<String, List<Catalog2Vo>> map = null;
        if (catalogJson == null) { // 缓存不命中查数据库
            //map = getCatalogJsonFromDBWithDistributeLock();
            map=getCatalogJsonFromDBWithRedisson();
            // 写入redis (先转为json String, 方便跨语言和平台，直接写入java数据结构其他语言获取不方便)
            ops.set(CATALOG_JSON, JSON.toJSONString(map));
        } else {
            // 缓存里有直接返回
            log.info("缓存中有二级三级分类数据，直接返回");
            map = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
        }
        return map;
    }
    /**
     * @description: 从数据库查询二级三级分类数据 加分布式锁
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithDistributeLock() {
        //log.info("缓存中没有二级三级分类数据，开始查询数据库");
        // 占分布式锁，去redis占坑（创建一个key为LOCK的键值对），百万并发进来，都去占坑，但只有一个能set成功  set key value NX命令
        // 占锁和设置过期时间必须同时, 且value是一个uuid（作为当前线程的唯一标识，删锁时核对防止删除别人的锁）
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(LOCK, uuid, 5, TimeUnit.MINUTES); // 设置过期时间防止异常或者断电导致死锁，过期时间设长一点，防止业务执行太久
        if (lock) { // 占锁成功
            log.info("获取分布式锁成功");
            String catalogJson=stringRedisTemplate.opsForValue().get(CATALOG_JSON);
            // 拿到锁先判断缓存里有没有，有直接释放锁返回
            if(catalogJson!=null){
                log.info("缓存中有二级三级分类数据，直接返回");
                releaseLock(uuid);
                return JSON.parseObject(catalogJson,new TypeReference<Map<String, List<Catalog2Vo>>>(){});
            }
            Map<String, List<Catalog2Vo>> map = null;
            try {
                map = getCatalogJsonByDB();
                stringRedisTemplate.opsForValue().set(CATALOG_JSON, JSON.toJSONString(map));
            } finally { // 无论业务执行成功或失败，都要解锁
                releaseLock(uuid);
                return map;
            }
        } else {  // 占锁失败，重试
            log.info("获取分布式锁失败，等待重试");
            try {
                Thread.sleep(200); // 等待200ms，一直调会OOM
            } catch (Exception e) {
            }
            return getCatalogJsonFromDBWithDistributeLock();
        }
    }
    /**
     * @description: 释放锁
     * @param:
     * @param uuid
     * @return: void
     **/
    public void releaseLock(String uuid){
        // 无论业务执行成功或失败，都要解锁
        // 释放锁 ：
        // 1.考虑业务代码太长时可能当前线程还没执行完锁就过期了，其他线程获取锁，等自己执行完却删了别的线程的锁  ->
        // 解决： 占锁时设置uuid，删锁时核对uuid确认是自己的锁
        // 2.考虑从redis获取再传输回来有时延，如下情况： 过期时间10s， 当前时刻9.8s，判断是自己的锁 ，传回来花了0.5s，导致传回来判断是自己的锁，但过期把锁删了，所以还是删的别的线程的锁
        // 解决： 判断和删锁也要同时->运行脚本删锁 参考：https://redis.io/commands/set/
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        // DefaultRedisScript<T> 泛型T为返回值泛型，删成功为1，失败为0
        stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(LOCK), uuid);
    }



    /**
     * @description: 使用Redisson加分布式锁
     * @param:
     * @return: List<Catalog2Vo>>
     **/
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedisson() {
        // 获取一把锁
        RLock lock=redissonClient.getLock("CatalogJson-Lock");
        lock.lock(); // 加锁 阻塞式等待  锁住lock()到unlock()间的所有代码
        log.info("获取分布式锁成功");
        String catalogJson=stringRedisTemplate.opsForValue().get(CATALOG_JSON);
        // 拿到锁先判断缓存里有没有，有直接释放锁返回
        if(catalogJson!=null){
            log.info("缓存中有二级三级分类数据，直接返回");
            lock.unlock();  // 释放锁
            return JSON.parseObject(catalogJson,new TypeReference<Map<String, List<Catalog2Vo>>>(){});
        }
        Map<String, List<Catalog2Vo>> map = null;
        try {
            map = getCatalogJsonByDB();
            stringRedisTemplate.opsForValue().set(CATALOG_JSON, JSON.toJSONString(map));
        } finally { // 无论业务执行成功或失败，都要解锁
            lock.unlock();  // 释放锁
            return map;
        }
    }
    /**
     * @description: 删除缓存注解（执行方法就删除相关缓存）
     * @param:
     * @param category
     * @return: java.lang.String
     **/
    @CacheEvict(value = {"product"},key="'getCatalogJsonByDBUseSpringCache'")
    @Override
    public String updateCategory(CategoryEntity category) {
        return getBaseMapper().updateById(category)>0?"success":"fail";
    }
    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 检查删除菜单是否被引用
        // 逻辑删除
        baseMapper.deleteBatchIds(asList);
    }
    /**
     * @description: 删除某一分区下的所有缓存
     * @param:
     * @param category
     * @return: java.lang.String
     **/
    @CacheEvict(value = {"product"},allEntries = true)
    public String updateCategory1(CategoryEntity category) {
        return getBaseMapper().updateById(category)>0?"success":"fail";
    }

    /**
     * @description:  @Caching组合多种缓存操作
     * @param:
     * @param category
     * @return: java.lang.String
     **/
    @Caching(evict = {
            @CacheEvict(value = {"product"},key="'getCatalogJsonByDBUseSpringCache'"),
            @CacheEvict(value = {"product"},key="'getCatalogJsonByDBUseSpringCache123'"),
    })
    public String updateCategory2(CategoryEntity category) {
        return getBaseMapper().updateById(category)>0?"success":"fail";
    }

    // 19:03

}