package com.vdian.mcall.dto;

import java.lang.reflect.Method;

/**
 * <p>文件名称：MethodDTO </p>
 * <p>文件描述：MCall存储的数据结构</p>
 * <p>版权所有：版权所有(C)2011-2099 </p>
 * <p>公   司：微店</p>
 * <p>完成日期：2017/6/12 </p>
 *
 * @author wangqiming
 */
public class MethodDTO<T> {

    private Method method;
    private Class<T> clazz;
    private String beanId;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }
}
