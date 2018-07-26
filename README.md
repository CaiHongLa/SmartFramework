# SmartFramework 1.0.0
## 轻量级的Web应用框架
### 特性：
```
1.基础的MVC控制器
2.集成Netty Http协议栈，提供Http服务
3.集成Netty自定义协议栈，基于观察者模式进行消息路由转发
4.RPC调用支持
5.轻量级分布式部署
6.服务注册、发现支持
7.基于Spring环境
```
### 配置文件引入
#### pom.xml
      
       <dependency>
            <groupId>cn.cloudwalk.smartframework</groupId>
            <artifactId>smart-rpc</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>cn.cloudwalk.smartframework</groupId>
            <artifactId>smart-http</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>cn.cloudwalk.smartframework</groupId>
            <artifactId>smart-config</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>cn.cloudwalk.smartframework</groupId>
            <artifactId>smart-task</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
             <groupId>cn.cloudwalk.smartframework</groupId>
             <artifactId>smart-mvc</artifactId>
             <version>1.0.0</version>
        </dependency>
        <dependency>
             <groupId>cn.cloudwalk.smartframework</groupId>
             <artifactId>smart-netty</artifactId>
             <version>1.0.0</version>
        </dependency>
        
#### applicationContext.xml
         <!-- 导入RPC支持 -->
         <import resource="classpath*:smartframework-rpc.xml"/>
  
         <!-- 导入Config支持 -->
         <import resource="classpath*:smartframework-config.xml"/>
        
         <!-- 导入Task支持 -->
         <import resource="classpath*:smartframework-task.xml"/>
         
#### application-cfg.properties
         #dev or deployment
         system.mode=deployment
         system.exceptionWrapper=cn.cloudwalk.smartframework.common.exception.wrapper.ProtocolExceptionWrapper
         system.exceptionWrapper.defaultErrorCode=40000
         
         #http settings
         system.http.params.maxTotal=5000
         system.http.params.defaultMaxPerRoute=5000
         system.http.params.connectTimeout=30000
         system.http.params.soTimeout=90000
         system.http.params.ioThreadCount=40
         system.http.params.backlogSize=512
         
#### distributed-config.properties
         zookeeper.url=172.16.12.210:2181
         zookeeper.id=netty
         zookeeper.localIp=192.168.200.146
         zookeeper.rootPath=/cloudwalk
         zookeeper.config.center=true
         
#### netty-cfg.properties
        #netty.ssl.certBasePath=/nettyCertPath
        netty.local.ip=192.168.3.66
        
        http.server.port=8003
        http.server.accepts.size=100
        http.server.boss.thread.size=1
        http.server.worker.thread.size=100
        http.fixed.thread.core.size=100
        http.fixed.thread.queue.size=65536
        http.exchange.heartbeat.time=60000
        http.exchange.heartbeat.timeout=300000
        
        netty.server.port=8004
        netty.client.connect.timeout=3000
        netty.client.connect.time=3000
        netty.server.accepts.size=100
        netty.server.boss.thread.size=1
        netty.server.worker.thread.size=100
        netty.fixed.thread.core.size=100
        netty.fixed.thread.queue.size=65536
        netty.exchange.heartbeat.time=60000
        netty.exchange.heartbeat.timeout=300000
        
        rpc.server.port=8002
        rpc.client.connect.timeout=3000
        rpc.client.connect.time=3000
        rpc.server.accepts.size=100
        rpc.server.boss.thread.size=1
        rpc.server.worker.thread.size=100
        rpc.fixed.thread.core.size=100
        rpc.fixed.thread.queue.size=65536
        rpc.exchange.heartbeat.time=60000
        rpc.exchange.heartbeat.timeout=300000
