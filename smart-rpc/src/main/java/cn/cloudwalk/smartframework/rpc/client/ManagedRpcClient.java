package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.clientcomponents.core.ManagedClient;
import cn.cloudwalk.smartframework.clientcomponents.core.util.Args;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.rpc.invoke.FutureSet;
import cn.cloudwalk.smartframework.transport.Client;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 可被管理的Rpc客户端
 *
 * @since 2.0.10
 */
public class ManagedRpcClient implements ManagedClient {

    private static final Logger logger = LogManager.getLogger(ManagedClient.class);

    /**
     * 具体的Rpc客户端
     */
    private final Client client;

    public ManagedRpcClient(Client client) {
        this.client = client;
    }

    @Override
    public Object send(Object request) {
        Args.notNull(client, "client");
        NettyRpcRequest request1 = (NettyRpcRequest) request;
        NettyRpcResponseFuture nettyRpcResponseFuture = null;
        if(!request1.getOneWay()) {
            nettyRpcResponseFuture = new NettyRpcResponseFuture(request1.getRequestId(), request1.getClassName(), request1.getMethodName());
            FutureSet.futureMap.put(request1.getRequestId(), nettyRpcResponseFuture);
        }
        try {
            client.send(request1, true);
        } catch (TransportException e) {
            logger.error(e);
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
        return nettyRpcResponseFuture;
    }

    @Override
    public boolean isClosed() {
        if (null == client) {
            return false;
        }
        return client.isClosed();
    }

    @Override
    public boolean isConnected() {
        if (null == client) {
            return false;
        }
        return client.isConnected();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        Args.notNull(client, "client");
        return client.getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        return getLocalAddress().getPort();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        Args.notNull(client, "client");
        return client.getRemoteAddress();
    }

    @Override
    public int getRemotePort() {
        return getRemoteAddress().getPort();
    }

    @Override
    public void close() {
        if (null != client) {
            client.close();
        }
    }
}
