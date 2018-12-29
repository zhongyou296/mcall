package com.zhongyou.mcall.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.zhongyou.mcall.MCall;
import com.zhongyou.mcall.dto.MethodDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * <p>文件名称：MCallProcessor </p>
 * <p>文件描述：对@MCall标记的处理器</p>
 * <p>完成日期：17/6/10 </p>
 *
 * @author zhongyou
 */
@Component
public class MCallProcessor implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MCallProcessor.class);

    private static ApplicationContext applicationContext;

    /**
     * key->methodName,value->MethodDTO
     */
    private static Map<String, MethodDTO> methodDTOMap = Maps.newConcurrentMap();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MCallProcessor.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        LOGGER.warn("MethodCallProcessor init success");
    }

    @PreDestroy
    public void destroy() {
        methodDTOMap = null;
        LOGGER.warn("MethodCallProcessor destroy success");
    }

    private static <T> T getBean(String beanName, Class<T> requiredType) {
        return applicationContext.getBean(beanName, requiredType);
    }

    /**
     * 查找bean，若存在，返回执行结果
     *
     * @param methodName
     */
    public static Map<String, Object> findBean(String methodName, Map<String, String> paramsMap) throws Exception {
        Preconditions.checkArgument(StringUtils.isNotBlank(methodName), "findBean param is invalid");
        // 先从缓存中查找是否存在该方法的DTO，若存在，直接执行；若不存在，再去ApplicationContext中查找
        if (methodDTOMap.get(methodName) == null) {
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                if (containsMethod(beanName, methodName)) {
                    break;
                }
            }
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        if (methodDTOMap.get(methodName) == null) {
            resultMap.put("result", "");
            resultMap.put("method", "");
            return resultMap;
        }
        MethodDTO<?> methodDTO = methodDTOMap.get(methodName);
        Object bean = applicationContext.getBean(methodDTO.getBeanId());
        // FIXME: 2017/6/13 获取动态代理类的父类
        Object superBean = AopTargetUtils.getTarget(bean);
        String method = superBean.getClass().getName() + "." + methodDTO.getMethod().getName();
        Object[] params = convertType(paramsMap, methodDTO.getMethod());
        Object result = invokeMethod(methodDTOMap.get(methodName), params);
        resultMap.put("method", method);
        resultMap.put("result", result);
        return resultMap;
    }

    /**
     * check方法名是否有效
     *
     * @param beanName
     * @param methodName
     * @return
     */
    private static Boolean containsMethod(String beanName, String methodName) throws Exception {
        Preconditions.checkArgument(StringUtils.isNotBlank(beanName), "containsMethod param is invalid");
        Preconditions.checkArgument(StringUtils.isNotBlank(methodName), "containsMethod param is invalid");
        Object bean = applicationContext.getBean(beanName);
        // FIXME: 2017/6/13 获取动态代理类的父类
        Object superBean = AopTargetUtils.getTarget(bean);
        Method method = findMethod(superBean.getClass(), methodName);
        if (method == null) {
            return false;
        }
        MethodDTO methodDTO = new MethodDTO();
        methodDTO.setBeanId(beanName);
        methodDTO.setMethod(method);
        methodDTO.setClazz(bean.getClass());
        // 将查询到的DTO插入cache
        methodDTOMap.put(methodName, methodDTO);
        return true;
    }

    /**
     * 执行方法
     * 注意:ApplicationContext中可能存在多个相同类型的bean
     *
     * @param dto
     * @param args
     * @return
     * @throws Exception
     */
    public static Object invokeMethod(MethodDTO dto, Object... args) throws Exception {
        Preconditions.checkNotNull(dto, "invokeMethod param is invalid");
        Object target = getBean(dto.getBeanId(), dto.getClazz());
        // 将方法访问权限设为public
        ReflectionUtils.makeAccessible(dto.getMethod());
        // 加入对静态方法的处理
        if (Modifier.isStatic(dto.getMethod().getModifiers())) {
            target = null;
        }
        Object result = ReflectionUtils.invokeMethod(dto.getMethod(), target, args);
        if (result == null) {
            return StringUtils.EMPTY;
        }
        return result;
    }

    /**
     * 通过方法名查找方法
     *
     * @param clazz
     * @param methodName
     * @return
     */
    public static <T> Method findMethod(Class<T> clazz, String methodName) throws Exception {
        Preconditions.checkNotNull(clazz, "findMethod param is invalid");
        Preconditions.checkNotNull(methodName, "findMethod param is invalid");
        Method[] methods = clazz.getDeclaredMethods();
        Method result = null;
        for (Method method : methods) {
            MCall mCall = method.getAnnotation(MCall.class);
            if (mCall != null && mCall.alias().equals(methodName)) {
                result = method;
                break;
            }
        }
        return result;
    }

    /**
     * 转化方法的参数类型
     *
     * @param paramsMap
     * @param method
     * @return
     */
    private static Object[] convertType(Map<String, String> paramsMap, Method method) throws Exception {
        LocalVariableTableParameterNameDiscoverer discover = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = discover.getParameterNames(method);
        Class<?>[] classes = method.getParameterTypes();
        Object[] result = new Object[paramNames.length];
        for (int i = 0; i < paramNames.length; ++i) {
            for (String paramKey : paramsMap.keySet()) {
                if (paramKey.equals(paramNames[i])) {
                    result[i] = JSON.parseObject(paramsMap.get(paramKey), classes[i]);
                }
            }
        }
        return result;
    }

    /**
     * 获取methodDTOMap集合
     *
     * @return
     */
    public static Map<String, MethodDTO> getMethodDTOMap() {
        return methodDTOMap;
    }

    /**
     * 添加一个methodDTO元素
     *
     * @param key
     * @param dto
     */
    public static void add(String key, MethodDTO dto) {
        methodDTOMap.put(key, dto);
    }

    /**
     * 获取一个methodDTO元素
     *
     * @param key
     * @return
     */
    public static MethodDTO get(String key) {
        return methodDTOMap.get(key);
    }

    /**
     * 删除一个methodDTO元素
     *
     * @param key
     */
    public static void remove(String key) {
        methodDTOMap.remove(key);
    }

    /**
     * 判断某个methodDTO元素是否已经存在
     *
     * @param key
     * @return
     */
    public static Boolean containsDTO(String key) {
        return methodDTOMap.get(key) != null ? true : false;
    }
}
