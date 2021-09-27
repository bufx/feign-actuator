> 收集注解为@feignclient的接口

## 使用方法

1. 在pom.xml中引入依赖
```xml
<dependency>
  <groupId>com.buubiu</groupId>
  <artifactId>feign-actuator</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```
2. 在配置文件添加以下配置
- yaml:
```yaml
#指定收集数据的地址，目前只支持单节点
collect-server: http://10.10.102.44:8889
```

- properties:
```properties
#指定收集数据的地址，目前只支持单节点
collect-server=http://10.10.102.44:8889
```

## 注意
1. 启动工程前要保证`收集`地址可用，否则无法收集相关数据
2. 若工程没有使用 `@FeignClient` 注解，不需要引入此SDK

