package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponse;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;

/**
 * Rpc invoker
 * 2018/8/18 12:21
 *
 * @author liyanhui(liyanhui @ cloudwalk.cn)
 * @since 2.0.10
 */
public class RpcInvoker<T> implements Invoker<T> {

    private final Class<T> type;

    private String zookeeperId;

    private IZookeeperService zookeeperService;

    public RpcInvoker(Class<T> type, String zookeeperId, IZookeeperService zookeeperService) {
        this(type, false, false, zookeeperId, zookeeperService);
    }

    public RpcInvoker(Class<T> type, boolean async, boolean oneWay, String zookeeperId, IZookeeperService zookeeperService) {
        this.type = type;
        this.zookeeperId = zookeeperId;
        this.zookeeperService = zookeeperService;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean isOneWay() {
        return false;
    }

    @Override
    public boolean isBroadcast() {
        return false;
    }

    @Override
    public String getZookeeperId() {
        return zookeeperId;
    }

    @Override
    public IZookeeperService getZookeeperService() {
        return zookeeperService;
    }

    @Override
    public Result invoke(Invocation invocation) {
        NettyRpcRequest request = new NettyRpcRequest();
        request.setParameterTypes(invocation.getParameterTypes());
        request.setParameters(invocation.getArguments());
        NettyRpcResponseFuture nettyRpcResponseFuture = RequestHelper.sendRequest(invocation.getTargetIp(), invocation.getTargetPort(), invocation.getClassName(), invocation.getMethodName(), request);
        NettyRpcResponse response = nettyRpcResponseFuture.get();
        RpcResult result = new RpcResult(response.getResult());
        result.setException(response.getError());
        return result;
    }
}
