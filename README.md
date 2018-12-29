一、配置公司自己的私服地址，将该工具deploy到公司私服环境中

二、为需要使用该工具的应用添加pom依赖
```xml
<dependency>
    <groupId>com.zhongyou.mcall</groupId>
    <artifactId>mcall</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
    
三、在Spring的配置文件中添加MCall
```xml
<import resource="classpath:mcall-application.xml"/>
```
    
四、在web.xml加入servlet过滤器
```xml
<!--用于处理MCall相关请求-->
<servlet>
    <servlet-name>MCallServlet</servlet-name>
    <servlet-class>MCallServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>MCallServlet</servlet-name>
    <url-pattern>/MCall</url-pattern>
</servlet-mapping>
```

五、在方法名上加上注解，注意必须是Spring的Bean
示例：
```java
@Component
public class HDFSManager {
    
    // 你只需要加上一个注解
    @MCall(alias = "exists")
    public boolean exists(String path) {
        try {
            return fs.exists(new Path(path));
        } catch (IOException e) {
            initFileSystem();
            try {
                return fs.exists(new Path(path));
            } catch (IOException e1) {
                log.error("查询路径是否存在失败！", e1);
            }
        }
        return false;
    }
}
```
六、请求格式    
    http://127.0.0.1:8080/MCall?methodName=xx&params={paramName1:xx,paramName2:xx}
示例：
    http://127.0.0.1:8080/MCall?methodName=exists&params={"path":"/user/ferrari"}
返回结果：

    {
      "result": false,
      "method": "com.zhongyou.ferrari.hdfs.HDFSManager.exists"
    }
说明：result是方法执行的结果,method是具体执行的方法名

目前支持普通方法调用，支持基本类型，支持包装类型，支持对象访问，支持Spring的AOP机制。
注意：
1、目前不支持Date类型；
2、不支持同名方法。
