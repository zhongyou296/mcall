package com.zhongyou.mcall;

import java.lang.annotation.*;

/**
 * <p>文件名称：MCall </p>
 * <p>文件描述：@MCall标记</p>
 * <p>完成日期：2017/6/8 </p>
 *
 * @author zhongyou
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MCall {

    String alias() default "";
}
