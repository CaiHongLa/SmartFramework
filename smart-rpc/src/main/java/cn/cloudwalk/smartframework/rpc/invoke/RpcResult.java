package cn.cloudwalk.smartframework.rpc.invoke;

/**
 * Rpc响应结果
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午6:21
 * @since 2.0.10
 */
public class RpcResult implements Result {

    private Object value;
    private Throwable exception;

    public RpcResult(Object result) {
        this.value = result;
    }

    public RpcResult(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public boolean hasException() {
        return exception != null;
    }

    public void setException(Throwable exception){
        this.exception = exception;
    }

    @Override
    public Object getValueIfHasException() throws Throwable {
        if (exception != null) {
            throw exception;
        }
        return value;
    }

    @Override
    public String toString() {
        return "RpcResult{" +
                "value=" + value +
                ", exception=" + exception +
                '}';
    }
}
