package com.atguigu.product.valid;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;
/**
 * @Author qiang.long
 * @Date 2024/03/22
 * @Description @ListValue的校验器
 * 该类的2个泛型：
 * （1）指定实现的注解
 * （2）指定校验的数据类型
 *
 * 实现两个方法：
 * （1）initialize ：初始化方法
 * （2）isValid ：进行校验
 **/
@Slf4j
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private Set<Integer> set=new HashSet<>();

    /**
     * @description: 初始化方法，用于获取@ListValue注解的详细信息，比如vals
     * @param:
     * @param constraintAnnotation
     * @return: void
     **/
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals= constraintAnnotation.vals();
        if (vals.length==0){
            log.error("@ListValue注解限定值为空");
        }
        for (int val : vals) {
            set.add(val);
        }
    }
    /**
     * @description: 判断是否校验成功
     * @param:
     * @param value @ListValue注解标注的字段（待校验的值）
     * @param context
     * @return: boolean
     **/
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
