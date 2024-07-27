package com.atguigu.product.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.product.dao.NodeInfoDao;
import com.atguigu.product.entity.NodeInfo;
import com.atguigu.product.service.NodeInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
/**
 * @Author qiang.long
 * @Date 2024/04/05
 * @Description  @EnableScheduling 开启定时任务
 **/
@EnableScheduling
@Slf4j
@Service("nodeInfoService")
public class NodeInfoServiceImpl extends ServiceImpl<NodeInfoDao, NodeInfo> implements NodeInfoService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String SOPHGO_NODE="sophgo";
    private static final String NVIDIA_NODE="nvidia";
    private static final String DCU_NODE="dcu";

    @Scheduled(cron = "0 0/3 * * * *")
    public void sync(){
        log.info("---sync cache---");
        List<NodeInfo> nodeInfoList=getBaseMapper().selectList(null);
        Arrays.asList(SOPHGO_NODE).forEach(tpu->{
            // 真实
            Set<String> set=nodeInfoList.stream().filter(nodeInfo -> nodeInfo.getTpuType()!=null&&nodeInfo.getTpuType().contains(tpu)).map(NodeInfo::getNodeIp).collect(Collectors.toSet());
            // redis中
            String redisSet=stringRedisTemplate.opsForValue().get(tpu);
            if(redisSet!=null&&JSON.parseObject(redisSet,new TypeReference<HashSet<String>>(){}).equals(set)){
            }else{
                log.info("{} TPU更新，redis：{},实际：{}",tpu,redisSet,JSON.toJSON(set));
                stringRedisTemplate.opsForValue().set(tpu,JSON.toJSONString(set));
            }
        });

        Arrays.asList(NVIDIA_NODE,DCU_NODE).forEach(gpu->{
            // 真实
            Set<String> set=nodeInfoList.stream().filter(nodeInfo -> nodeInfo.getGpuType()!=null&&nodeInfo.getGpuType().contains(gpu)).map(NodeInfo::getNodeIp).collect(Collectors.toSet());
            // redis中
            String redisSet=stringRedisTemplate.opsForValue().get(gpu);
            if(redisSet!=null&&JSON.parseObject(redisSet,new TypeReference<HashSet<String>>(){}).equals(set)){
            }else{
                log.info("{} GPU更新，redis：{},实际：{}",gpu,redisSet,JSON.toJSON(set));
                stringRedisTemplate.opsForValue().set(gpu,JSON.toJSONString(set));
            }
        });
    }


}
