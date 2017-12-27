一、添加pom依赖

    <dependency>
        <groupId>com.vdian.mcall</groupId>
        <artifactId>mcall</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
二、添加MCall的Sring配置

    <import resource="classpath:mcall-application.xml"/>
三、在web.xml加入servlet过滤器

    <!--用于处理MCall相关请求-->
    <servlet>
        <servlet-name>MCallServlet</servlet-name>
        <servlet-class>com.vdian.mcall.servlet.MCallServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>MCallServlet</servlet-name>
        <url-pattern>/MCall</url-pattern>
    </servlet-mapping>
四、在方法名上加上注解
示例：

     @MCall(alias = "exists")
     public boolean exists(String path) {
        try {
            log.info("path={}", path);
            return fs.exists(new Path(path));
        } catch (IOException e) {
            log.error("查询路径是否存在失败！", e);
        }
        return false;
     }
五、请求格式    

    http://127.0.01:8080/MCall?methodName=xx&params={paramName1:xx,paramName2:xx}
示例：

    http://127.0.01:8080/MCall?methodName=exists&params={"path":"/user/ferrari"}

返回结果：

    {
      "result": false,
      "method": "com.vdian.ferrari.hdfs.HDFSManager.exists"
    }
说明：result是方法执行的结果,method是具体执行的方法名

目前支持普通方法调用，支持基本类型，支持包装类型，支持对象访问，支持Spring的AOP机制。
注意：
1、目前不支持Date类型；
2、不支持同名方法。
