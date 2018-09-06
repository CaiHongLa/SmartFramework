package cn.cloudwalk.smartframework.transport.support;

/**
 * 常量定义
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface ProtocolConstants {

    //***********************Http服务参数***********************

    /**
     * Http服务绑定端口
     */
    String HTTP_SERVER_PORT = "http.server.port";

    /**
     * Http服务器可以接受的最大连接数
     */
    String HTTP_SERVER_ACCEPTS = "http.server.accepts.size";

    /**
     * Http服务器Boss线程数
     */
    String HTTP_SERVER_BOSS_THREAD_SIZE = "http.server.boss.thread.size";

    /**
     * Http服务器Worker线程数
     */
    String HTTP_SERVER_WORKER_THREAD_SIZE = "http.server.worker.thread.size";

    /**
     * Http FixThreadPool核心线程数
     */
    String HTTP_FIXED_THREAD_POOL_CORE_SIZE = "http.fixed.thread.core.size";

    /**
     * Http FixThreadPool线程队列数
     */
    String HTTP_FIXED_THREAD_POOL_QUEUE_SIZE = "http.fixed.thread.queue.size";

    /**
     * Http数据交换层心跳间隔
     */
    String HTTP_EXCHANGE_HEART_BEAT_TIME = "http.exchange.heartbeat.time";

    /**
     * Http数据交换层心跳超时事件
     */
    String HTTP_EXCHANGE_HEART_BEAT_TIMEOUT = "http.exchange.heartbeat.timeout";

    /**
     * Http Disruptor
     */
    String HTTP_DISRUPTOR_SWITCH = "http.disruptor";


    //***********************Netty服务参数************************

    /**
     * Netty服务绑定端口
     */
    String NETTY_SERVER_PORT = "netty.server.port";

    /**
     * Netty服务器可以接受的最大连接数
     */
    String NETTY_SERVER_ACCEPTS = "netty.server.accepts.size";

    /**
     * Netty服务器Boss线程数
     */
    String NETTY_SERVER_BOSS_THREAD_SIZE = "netty.server.boss.thread.size";

    /**
     * Netty服务器Worker线程数
     */
    String NETTY_SERVER_WORKER_THREAD_SIZE = "netty.server.worker.thread.size";

    /**
     * Netty FixThreadPool核心线程数
     */
    String NETTY_FIXED_THREAD_POOL_CORE_SIZE = "netty.fixed.thread.core.size";

    /**
     * Netty FixThreadPool线程队列数
     */
    String NETTY_FIXED_THREAD_POOL_QUEUE_SIZE = "netty.fixed.thread.queue.size";

    /**
     * Netty数据交换层心跳间隔
     */
    String NETTY_EXCHANGE_HEART_BEAT_TIME = "netty.exchange.heartbeat.time";

    /**
     * Netty数据交换层心跳超时事件
     */
    String NETTY_EXCHANGE_HEART_BEAT_TIMEOUT = "netty.exchange.heartbeat.timeout";

    /**
     * Netty Disruptor
     */
    String NETTY_DISRUPTOR_SWITCH = "netty.disruptor";


    //****************** Rpc Server *********************
    /**
     * Rpc 服务绑定端口
     */
    String RPC_SERVER_PORT = "rpc.server.port";

    /**
     * Rpc 服务器可以接受的最大连接数
     */
    String RPC_SERVER_ACCEPTS_SIZE = "rpc.server.accepts.size";

    /**
     * Rpc 服务器Boss线程数
     */
    String RPC_SERVER_BOSS_THREAD_SIZE = "rpc.server.boss.thread.size";

    /**
     * Rpc 服务器Worker线程数
     */
    String RPC_SERVER_WORKER_THREAD_SIZE = "rpc.server.worker.thread.size";

    /**
     * Rpc FixThreadPool核心线程数
     */
    String RPC_FIXED_THREAD_CORE_SIZE = "rpc.fixed.thread.core.size";

    /**
     * Rpc FixThreadPool线程队列数
     */
    String RPC_FIXED_THREAD_QUEUE_SIZE = "rpc.fixed.thread.queue.size";

    /**
     * Rpc 数据交换层心跳间隔
     */
    String RPC_EXCHANGE_HEARTBEAT_TIME = "rpc.exchange.heartbeat.time";

    /**
     * Rpc 数据交换层心跳超时事件
     */
    String RPC_EXCHANGE_HEARTBEAT_TIMEOUT = "rpc.exchange.heartbeat.timeout";

    /**
     * Rpc 客户端建立连接时的等待服务器响应时间
     */
    String RPC_CLIENT_CONNECT_TIMEOUT = "rpc.client.connect.timeout";

    /**
     * Rpc 客户端建立连接时可用时间，超过这个时间没有建立连接，会断开连接
     */
    String RPC_CLIENT_CONNECT_TIME = "rpc.client.connect.time";

    /**
     * Rpc 客户端最大连接数
     */
    String RPC_CLIENT_MAX_TOTOL = "rpc.client.maxTotal";

    /**
     * Rpc 客户端每个路由最大连接
     */
    String RPC_CLIENT_MAX_PER_ROUTE = "rpc.client.maxPerRoute";

    /**
     * Rpc 客户端连接最大空闲时间 超过时间未更新的连接将被关闭
     */
    String RPC_CLIENT_MAX_IDLE_TIME = "rpc.client.maxIdleTime";

    /**
     * Rpc 客户端连接最大存活时间 超过设置时间的连接将被关闭
     */
    String RPC_CLIENT_MAX_TIME_LIVE = "rpc.client.maxLiveTime";

    /**
     * Rpc Disruptor
     */
    String RPC_DISRUPTOR_SWITCH = "rpc.disruptor";



    //****************公共参数*********************************

    /**
     * 服务器可以接受的最大连接数
     */
    String SERVER_ACCEPTS = "server.accepts.size";

    /**
     * 客户端建立连接时的等待服务器响应时间
     */
    String CLIENT_CONNECT_TIMEOUT = "client.connect.timeout";

    /**
     * 客户端建立连接时可用时间，超过这个时间没有建立连接，会断开连接
     */
    String CLIENT_CONNECT_TIME = "client.connect.time";

    /**
     * FixThreadPool核心线程数
     */
    String FIXED_THREAD_POOL_CORE_SIZE = "fixed.thread.core.size";

    /**
     * FixThreadPool线程队列数
     */
    String FIXED_THREAD_POOL_QUEUE_SIZE = "fixed.thread.queue.size";

    /**
     * 数据交换层心跳间隔
     */
    String EXCHANGE_HEART_BEAT_TIME = "exchange.heartbeat.time";

    /**
     * 数据交换层心跳超时事件
     */
    String EXCHANGE_HEART_BEAT_TIMEOUT = "exchange.heartbeat.timeout";

    /**
     * Disruptor消费者线程池名称
     */
    String DISRUPTOR_CONSUMER_POOL_NAME = "disruptor.consumer.pool.name";

    /**
     * 是否使用Disruptor
     */
    String DISRUPTOR_SWITCH = "disruptor.switch";

}
