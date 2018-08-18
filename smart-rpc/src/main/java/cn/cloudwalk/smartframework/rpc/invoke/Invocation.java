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
     * 请求类名
     *
     * @return 类名
     */
    String getClassName();

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
     * 目标地址ip
     *
     * @return ip
     */
    String getTargetIp();

    /**
     * 目标地址端口
     *
     * @return port
     */
    int getTargetPort();

}
