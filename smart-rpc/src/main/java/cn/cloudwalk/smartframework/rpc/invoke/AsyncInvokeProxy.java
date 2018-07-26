package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.rpc.invoke.future.NettyRpcResponseFuture;

/**
 * Rpc异步调用
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface AsyncInvokeProxy {

   <V> NettyRpcResponseFuture<V> asyncCall(Class<V> type, String methodName, Object[] args);

   void asyncOneWayCall(String methodName, Object[] args);
}
