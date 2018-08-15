package cn.cloudwalk.smartframework.common.distributed;

import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;

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
