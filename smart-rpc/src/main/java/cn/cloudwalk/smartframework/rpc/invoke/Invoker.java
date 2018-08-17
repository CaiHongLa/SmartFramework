package cn.cloudwalk.smartframework.rpc.invoke;

/**
 * Rpc反射调用 代表一个可以反射的类
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午5:57
 * @since 2.0.10
 */
public interface Invoker<T> {


    /**
     * 获取服务借口
     *
     * @return interface
     */
    Class<T> getInterface();

    /**
     * 调用
     *
     * @param invocation 调用实体
     * @return Result
     * @throws Throwable 调用异常
     */
    Result invoke(Invocation invocation) throws Throwable;
}
