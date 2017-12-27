package com.vdian.mcall;

import java.lang.annotation.*;

/**
 * <p>文件名称：MCall </p>
 * <p>文件描述：@MethodCall标记</p>
 * <p>版权所有：版权所有(C)2011-2099 </p>
 * <p>公   司：微店</p>
 * <p>完成日期：2017/6/8 </p>
 *
 * @author wangqiming
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MCall {

    String alias() default "";
}
