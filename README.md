# SmartFramework 2.0.0
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
            <version>2.0.0</version>
        </dependency>
        <dependency>
            <groupId>cn.cloudwalk.smartframework</groupId>
            <artifactId>smart-http</artifactId>
            <version>2.0.0</version>
        </dependency>
        <dependency>
            <groupId>cn.cloudwalk.smartframework</groupId>
            <artifactId>smart-config</artifactId>
            <version>2.0.0</version>
        </dependency>
        <dependency>
            <groupId>cn.cloudwalk.smartframework</groupId>
            <artifactId>smart-task</artifactId>
            <version>2.0.0</version>
        </dependency>
        <dependency>
             <groupId>cn.cloudwalk.smartframework</groupId>
             <artifactId>smart-mvc</artifactId>
             <version>2.0.0</version>
        </dependency>
        <dependency>
             <groupId>cn.cloudwalk.smartframework</groupId>
             <artifactId>smart-netty</artifactId>
             <version>2.0.0</version>
        </dependency>
        
#### applicationContext.xml
         <!-- 导入RPC支持 -->
         <import resource="classpath*:smartframework-rpc.xml"/>
  
         <!-- 导入Config支持 -->
         <import resource="classpath*:smartframework-config.xml"/>
        
         <!-- 导入Task支持 -->
         <import resource="classpath*:smartframework-task.xml"/>
         
#### application.properties
         #dev or deployment
         system.mode=deployment
         system.localIp=10.*
         system.exceptionWrapper=cn.cloudwalk.smartframework.common.exception.wrapper.ProtocolExceptionWrapper
         system.exceptionWrapper.defaultErrorCode=40000
         system.exceptionHttpStatus=200
         
         #http settings
         system.http.params.maxTotal=5000
         system.http.params.defaultMaxPerRoute=5000
         system.http.params.connectTimeout=30000
         system.http.params.soTimeout=90000
         system.http.params.ioThreadCount=40
         system.http.params.backlogSize=512
         
         #zookeeper
         zookeeper.url=10.10.1.113:2181
         zookeeper.id=netty
         zookeeper.rootPath=/yunying
         zookeeper.service.strategy=cn.cloudwalk.smartframework.rpc.service.discovery.adapter.RandomServiceDiscoveryStrategy
         zookeeper.config.center=true
         
         #netty rpc
         #netty.ssl.cert=/nettyCertPath
         http.server.port=8003
         http.server.accepts.size=0
         #http.server.boss.thread.size=1
         #http.server.worker.thread.size=100
         #http.fixed.thread.core.size=100
         http.fixed.thread.queue.size=65536
         http.exchange.heartbeat.time=60000
         http.exchange.heartbeat.timeout=300000
         http.disruptor=0
         
         netty.server.port=8004
         netty.server.accepts.size=0
         #netty.server.boss.thread.size=1
         #netty.server.worker.thread.size=100
         #netty.fixed.thread.core.size=100
         netty.fixed.thread.queue.size=65536
         netty.exchange.heartbeat.time=60000
         netty.exchange.heartbeat.timeout=300000
         netty.disruptor=0
         
         rpc.http.server.port=8002
         rpc.http.server.accepts.size=0
         #rpc.http.server.boss.thread.size=1
         #rpc.http.server.worker.thread.size=100
         #rpc.http.fixed.thread.core.size=100
         rpc.http.fixed.thread.queue.size=65536
         rpc.http.exchange.heartbeat.time=60000
         rpc.http.exchange.heartbeat.timeout=300000
         rpc.disruptor=0
         
         #data source
         ds.use=master
         ds.major=master
         ds.strategy=cn.cloudwalk.smartframework.core.dao.datasource.AnnotationBasedStrategy
         
         #master datasource
         ds.master.type=c3p0
         ds.master.driverClass=com.mysql.jdbc.Driver
         ds.master.jdbcUrl=jdbc:mysql://192.168.40.12:3306/cw_falcon?useUnicode=true&characterEncoding=UTF-8
         ds.master.user=root
         ds.master.password=root
         ds.master.initialPoolSize=10
         ds.master.maxIdleTime=600
         ds.master.maxPoolSize=10
         ds.master.minPoolSize=5
         ds.master.idleConnectionTestPeriod=600
         
####log4j2.xml
         <?xml version="1.0" encoding="UTF-8"?>
         <Configuration status="WARN" monitorInterval="60">
             <properties>
                 <property name="LOG_HOME">${sys:catalina.home}/logs/</property>
                 <property name="FILE_NAME">cloud-eagle-gateway</property>
             </properties>
         
             <Appenders>
                 <Console name="Console" target="SYSTEM_OUT">
                     <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} cloud-eagle-gateway %level %t | %c{1.} - %msg%n" />
                 </Console>
         
                 <RollingRandomAccessFile name="RollingRandomAccessFile" fileName="${LOG_HOME}/${FILE_NAME}.log" filePattern="${LOG_HOME}/$${date:yyyy-MM}/${FILE_NAME}-%d{yyyy-MM-dd HH-mm}-%i.log">
                     <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} cloud-eagle-gateway %level %t | %c{1.} - %msg%n"/>
                     <Policies>
                         <SizeBasedTriggeringPolicy size="50 MB"/>
                     </Policies>
                     <DefaultRolloverStrategy max="20"/>
                 </RollingRandomAccessFile>
         
                 <Async name="AsyncAppender">
                     <AppenderRef ref="RollingRandomAccessFile"/>
                     <AppenderRef ref="Console"/>
                 </Async>
             </Appenders>
         
             <Loggers>
                 <Root level="info">
                     <AppenderRef ref="Console" />
                     <AppenderRef ref="RollingRandomAccessFile" />
                 </Root>
         
                 <Logger name="RollingRandomAccessFileLogger" level="info" additivity="false">
                     <AppenderRef ref="AsyncAppender" />
                 </Logger>
             </Loggers>
         </Configuration>

