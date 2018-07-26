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
     * Netty客户端建立连接时的等待服务器响应时间
     */
    String NETTY_CLIENT_CONNECT_TIMEOUT = "netty.client.connect.timeout";

    /**
     * Netty客户端建立连接时可用时间，超过这个时间没有建立连接，会断开连接
     */
    String NETTY_CLIENT_CONNECT_TIME = "netty.client.connect.time";

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


    //******************Http Rpc Server *********************
    /**
     * Rpc Http服务绑定端口
     */
    String RPC_HTTP_SERVER_PORT = "rpc.http.server.port";

    /**
     * Rpc Http服务器可以接受的最大连接数
     */
    String RPC_HTTP_SERVER_ACCEPTS = "rpc.http.server.accepts.size";

    /**
     * Rpc Http服务器Boss线程数
     */
    String RPC_HTTP_SERVER_BOSS_THREAD_SIZE = "rpc.http.server.boss.thread.size";

    /**
     * Rpc Http服务器Worker线程数
     */
    String RPC_HTTP_SERVER_WORKER_THREAD_SIZE = "rpc.http.server.worker.thread.size";

    /**
     * Rpc  Http FixThreadPool核心线程数
     */
    String RPC_HTTP_FIXED_THREAD_POOL_CORE_SIZE = "rpc.http.fixed.thread.core.size";

    /**
     * Rpc Http FixThreadPool线程队列数
     */
    String RPC_HTTP_FIXED_THREAD_POOL_QUEUE_SIZE = "rpc.http.fixed.thread.queue.size";

    /**
     * Rpc Http数据交换层心跳间隔
     */
    String RPC_HTTP_EXCHANGE_HEART_BEAT_TIME = "rpc.http.exchange.heartbeat.time";

    /**
     * Rpc Http数据交换层心跳超时事件
     */
    String RPC_HTTP_EXCHANGE_HEART_BEAT_TIMEOUT = "rpc.http.exchange.heartbeat.timeout";

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
