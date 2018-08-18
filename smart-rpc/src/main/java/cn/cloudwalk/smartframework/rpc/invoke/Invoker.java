package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;

/**
 * Rpc反射调用 代表一个可以反射的类
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午5:57
 * @since 2.0.10
 */
public interface Invoker<T> {

    /**
     * 每个invoker都需要拿到zookeeper服务
     *
     * @return IZookeeperService
     */
    IZookeeperService getZookeeperService();

    /**
     * 每个invoker都需要指定zookeeperId
     *
     * @return zookeeperId
     */
    String getZookeeperId();

    /**
     * 获取服务借口
     *
     * @return interface
     */
    Class<T> getInterface();

    /**
     * 是否异步返回结果
     *
     * @return async
     */
    boolean isAsync();

    /**
     * 是否单向
     *
     * @return one way
     */
    boolean isOneWay();

    /**
     * 是否广播调用所有可用节点
     *
     * @return broadcast
     */
    boolean isBroadcast();

    /**
     * 调用
     *
     * @param invocation 调用实体
     * @return Result
     * @throws Throwable 调用异常
     */
    Result invoke(Invocation invocation) throws Throwable;
}
