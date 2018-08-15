package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 调用结果集合。
 * 每次发起rpc调用需要将requestId和调用结果存放。
 * 当调用完成返回结果时再取出来继续操作。
 *
 * @author 李延辉
 * @see NettyRpcResponseFuture
 * @since 1.0.0
 */
class FutureSet {

    static Map<String, NettyRpcResponseFuture> futureMap = new ConcurrentHashMap<>();

}
