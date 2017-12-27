package com.vdian.mcall.processor;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * <p>文件名称：AopTargetUtils </p>
 * <p>文件描述：</p>
 * <p>版权所有：版权所有(C)2011-2099 </p>
 * <p>公   司：微店</p>
 * <p>内容摘要：获取动态代理生成的Bean的父类对象的工具类</p>
 * <p>其他说明：</p>
 * <p>完成日期：2017/6/13</p>
 *
 * @author wangqiming
 */
public class AopTargetUtils {

    public static Object getTarget(Object proxy) throws Exception {
        // 非代理对象
        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }
        // JDK实现的代理对象
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else {
            // cglib实现的代理对象
            return getCglibProxyTargetObject(proxy);
        }
    }

    /**
     * 获取jdk动态代理生成的Bean的父类对象
     *
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();

        return target;
    }

    /**
     * 获取CGLIB动态代理生成的Bean的父类对象
     *
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);

        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();

        return target;
    }
}
