package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;

/**
 * Rpc响应结果
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午6:21
 * @since 2.0.10
 */
public class RpcResult {

    private Object value;
    private Throwable exception;
    private NettyRpcResponseFuture future;

    public RpcResult(NettyRpcResponseFuture future) {
        this.future = future;
    }

    public RpcResult(Object result) {
        this.value = result;
    }

    public RpcResult(Throwable exception) {
        this.exception = exception;
    }

    public Object getValue() {
        return value;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean hasException() {
        return exception != null;
    }

    public void setException(Throwable exception){
        this.exception = exception;
    }

    public void setValue(Object value){
        this.value = value;
    }

    public NettyRpcResponseFuture getFuture() {
        return future;
    }

    public void setFuture(NettyRpcResponseFuture future) {
        this.future = future;
    }

    public Object getValueIfHasException() throws Throwable {
        if (hasException()) {
            throw exception;
        }
        return value;
    }

    @Override
    public String toString() {
        return "RpcResult{" +
                "value=" + value +
                ", exception=" + exception +
                ", future=" + future +
                '}';
    }
}
