package cn.cloudwalk.smartframework.rpc.invoke;

/**
 * 可以反射的实体
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午6:06
 * @since 2.0.10
 */
public interface Invocation {

    /**
     * 方法
     *
     * @return 方法名
     */
    String getMethodName();

    /**
     * 参数类型
     *
     * @return 参数类型集合
     */
    Class<?>[] getParameterTypes();

    /**
     * 参数
     *
     * @return 参数集合
     */
    Object[] getArguments();

    /**
     * Invoker
     *
     * @return invoker.
     */
    Invoker<?> getInvoker();

}
