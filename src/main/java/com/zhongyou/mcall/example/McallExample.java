package com.zhongyou.mcall.example;

import com.zhongyou.mcall.MCall;
import org.springframework.stereotype.Component;

/**
 * <p>文件名称：McallExample </p>
 * <p>文件描述：MCall示例代码</p>
 * <p>完成日期：2017/6/12 </p>
 *
 * @author zhongyou
 */
@Component
public class McallExample {

    @MCall
    public String echoHello() {
        return "hello";
    }
}
