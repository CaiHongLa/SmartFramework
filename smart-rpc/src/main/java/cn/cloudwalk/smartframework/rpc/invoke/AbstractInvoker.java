package cn.cloudwalk.smartframework.rpc.invoke;

/**
 * 抽象Invoker
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午6:14
 * @since 2.0.10
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {

    private final Class<T> type;

    public AbstractInvoker(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation invocation) throws Throwable {
        RpcInvocation rpcInvocation = (RpcInvocation) invocation;
        rpcInvocation.setInvoker(this);
        return doInvoke(rpcInvocation);
    }

    protected abstract Result doInvoke(Invocation invocation) throws Throwable;
}
