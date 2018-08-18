package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;

/**
 *
 * Rpc请求上下文 临时存储数据
 *
 * 2018/8/18 17:12
 *
 * @author liyanhui(liyanhui @ cloudwalk.cn)
 * @since 2.0.10
 */
public class RpcContext {

    private NettyRpcResponseFuture future;

    /**
     * 线程临时变量存储当前线程的RpcContext
     */
    private static final ThreadLocal<RpcContext> LOCAL = ThreadLocal.withInitial(RpcContext::new);

    private RpcContext() {
    }

    public NettyRpcResponseFuture getFuture() {
        //在线程拿走存储的future之后删除
        removeContext();
        return future;
    }

    /**
     * 存储异步调用的Future
     *
     * @param future 异步调用的Future
     */
    public void setFuture(NettyRpcResponseFuture future) {
        this.future = future;
    }

    /**
     * 获取当前线程的RpcContext
     *
     * @return RpcContext
     */
    public static RpcContext getContext() {
        return LOCAL.get();
    }

    public static void restoreContext(RpcContext oldContext) {
        LOCAL.set(oldContext);
    }

    public static void removeContext() {
        LOCAL.remove();
    }

}
