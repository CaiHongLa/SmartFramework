package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponse;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Rpc invoker
 * 2018/8/18 12:21
 *
 * @author liyanhui(liyanhui @ cloudwalk.cn)
 * @since 2.0.10
 */
public class RpcInvoker<T> {

    private static final Logger logger = LogManager.getLogger(RpcInvoker.class);

    private final Class<T> type;

    private String zookeeperId;

    private IZookeeperService zookeeperService;

    public RpcInvoker(Class<T> type, String zookeeperId, IZookeeperService zookeeperService) {
        this.type = type;
        this.zookeeperId = zookeeperId;
        this.zookeeperService = zookeeperService;
    }

    public Class<T> getInterface() {
        return type;
    }

    public String getZookeeperId() {
        return zookeeperId;
    }

    public IZookeeperService getZookeeperService() {
        return zookeeperService;
    }

    public RpcResult invoke(RpcInvocation invocation) {
        logger.info("Ready to invoke remote service : " + invocation);
        NettyRpcRequest request = new NettyRpcRequest();
        request.setParameterTypes(invocation.getParameterTypes());
        request.setParameters(invocation.getArguments());
        RpcResult result = new RpcResult(new Object());
        if (invocation.isOneWay()) {
            RpcRequestHelper.sendRequestOneWay(invocation.getTargetIp(), invocation.getTargetPort(), invocation.getClassName(), invocation.getMethodName(), request);
            return result;
        }
        NettyRpcResponseFuture nettyRpcResponseFuture = RpcRequestHelper.sendRequest(invocation.getTargetIp(), invocation.getTargetPort(), invocation.getClassName(), invocation.getMethodName(), request);
        NettyRpcResponse response = nettyRpcResponseFuture.get();
        result.setValue(response.getResult());
        result.setException(response.getError());
        return result;
    }
}
