package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.clientcomponents.client.CloseableClient;
import cn.cloudwalk.smartframework.clientcomponents.client.TcpRoute;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponse;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Rpc invoker
 * 2018/8/18 12:21
 *
 * @author liyanhui(liyanhui @ cloudwalk.cn)
 * @since 2.0.10
 */
public class RpcInvoker<T> {

    private static final Logger logger = LogManager.getLogger(RpcInvoker.class);

    private boolean async;

    private final Class<T> type;

    private String zookeeperId;

    private IZookeeperService zookeeperService;

    public RpcInvoker(Class<T> type, String zookeeperId, IZookeeperService zookeeperService) {
        this(type, zookeeperId, zookeeperService, false);
    }

    public RpcInvoker(Class<T> type, String zookeeperId, IZookeeperService zookeeperService, boolean async) {
        this.type = type;
        this.zookeeperId = zookeeperId;
        this.zookeeperService = zookeeperService;
        this.async = async;
    }

    public boolean isAsync() {
        return async;
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
        logger.info("Ready to invoke remote service : " + invocation + " , async : " + async);
        NettyRpcRequest request = new NettyRpcRequest();
        request.setParameterTypes(invocation.getParameterTypes());
        request.setParameters(invocation.getArguments());
        request.setClassName(invocation.getClassName());
        request.setMethodName(invocation.getMethodName());
        request.setOneWay(invocation.isOneWay());
        request.setRequestId(UUID.randomUUID().toString());
        RpcResult result = new RpcResult(new Object());
        InetSocketAddress host = new InetSocketAddress(invocation.getTargetIp(), invocation.getTargetPort());
        CloseableClient closeableClient = RpcRequestHelper.getOrCreateClient(host);
        try {
            NettyRpcResponseFuture responseFuture = (NettyRpcResponseFuture) closeableClient.execute(new TcpRoute(host, host), request);
            if(invocation.isOneWay()) {
                return result;
            }
            if(async) {
                RpcContext.getContext().setFuture(responseFuture);
                return result;
            }
            NettyRpcResponse response = responseFuture.get();
            result.setValue(response.getResult());
            result.setException(response.getError());
            return result;
        } catch (IOException e) {
            logger.error("Error while do rpc invoke", e);
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }
}
