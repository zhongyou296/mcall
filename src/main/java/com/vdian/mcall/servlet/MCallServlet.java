package com.vdian.mcall.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.vdian.mcall.processor.MCallProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>文件名称：MCallServlet </p>
 * <p>文件描述：处理MCall相关的请求</p>
 * <p>版权所有：版权所有(C)2011-2099 </p>
 * <p>公   司：微店</p>
 * <p>完成日期：2017/6/12 </p>
 *
 * @author wangqiming
 */
public class MCallServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MCallServlet.class);

    /**
     * 参数格式: http://127.0.01:8080/MCall?methodName=xx&params={paramName1:xx,paramName2:xx}
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        // 获取方法名
        String methodName = request.getParameter("methodName");
        String paramsStr = request.getParameter("params");
        if (StringUtils.isBlank(methodName)) {
            LOGGER.error("invokeMethod param is invalid, methodName={}, paramsStr={}", methodName, paramsStr);
            out.println("参数有误，请检查参数格式...");
        }
        Map<String, Object> resultMap = invokeMethod(methodName, paramsStr);
        out.println(JSON.toJSONString(resultMap));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        // 获取方法名
        String methodName = request.getParameter("methodName");
        String paramsStr = request.getParameter("params");
        if (StringUtils.isBlank(methodName)) {
            LOGGER.error("invokeMethod param is invalid, methodName={}, paramsStr={}", methodName, paramsStr);
            out.println("参数有误，请检查参数格式...");
        }
        Map<String, Object> resultMap = invokeMethod(methodName, paramsStr);
        out.println(JSON.toJSONString(resultMap));
    }

    private Map<String, Object> invokeMethod(String methodName, String paramsStr) throws ServletException {
        Map<String, String> paramsMap = JSONObject.parseObject(paramsStr, HashMap.class);
        Map<String, String> paramsJsonMap = Maps.newHashMap();
        for (String paramKey : paramsMap.keySet()) {
            paramsJsonMap.put(paramKey, JSON.toJSONString(paramsMap.get(paramKey)));
        }
        try {
            return MCallProcessor.findBean(methodName, paramsJsonMap);
        } catch (Exception e) {
            LOGGER.error("invokeMethod error, methodName={}, paramsStr={}, e={}", methodName, paramsStr, e);
            throw new ServletException(e.getMessage());
        }
    }
}
