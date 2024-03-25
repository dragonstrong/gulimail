package com.atguigu.product.valid;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/**
 * @Author qiang.long
 * @Date 2024/03/22
 * @Description
 * @Constraint(validatedBy = { }) ：注解使用哪个校验器进行校验
 * @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })： 校验器可以标注在哪些位置（方法、属性...）
 * @Retention(RUNTIME) : 可以在运行时获取到
 *
 **/
@Documented
@Constraint(validatedBy = { ListValueConstraintValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ListValue {
    // 下面3个为注解必须有的属性

    /**
     * 校验失败返回信息
     **/
    String message() default "{com.atguigu.product.valid.ListValue.message}";

    /**
     * 分组校验
     **/

    Class<?>[] groups() default { };
    /**
     * 负载信息
     **/

    Class<? extends Payload>[] payload() default { };

    /**
     * Defines several {@code @NotBlank} constraints on the same element.
     *
     * @see NotBlank
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        NotBlank[] value();
    }

    // 以下为自定义属性
    int[] vals() default {};
}
