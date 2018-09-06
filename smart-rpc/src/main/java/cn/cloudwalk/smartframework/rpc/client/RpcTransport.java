package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.Client;
import cn.cloudwalk.smartframework.transport.Server;
import cn.cloudwalk.smartframework.transport.Transport;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;

/**
 * RpcTransport
 *
 * @author LIYANHUI
 * @since 2.0.10
 */
public class RpcTransport implements Transport {

    @Override
    public Server bind(TransportContext transportContext, ChannelHandler handler) {
        transportContext = transportContext.addParameter(ProtocolConstants.SERVER_ACCEPTS, transportContext.getParameter(ProtocolConstants.RPC_SERVER_ACCEPTS_SIZE));
        transportContext = transportContext.addParameter(ProtocolConstants.DISRUPTOR_CONSUMER_POOL_NAME, "rpc_channel_disruptor_consumer_pool");
        return new RpcServer(transportContext, handler);
    }

    @Override
    public Client connect(TransportContext transportContext, ChannelHandler handler) {
        transportContext = transportContext
                .addParameter(ProtocolConstants.CLIENT_CONNECT_TIME, transportContext.getParameter(ProtocolConstants.RPC_CLIENT_CONNECT_TIME))
                .addParameter(ProtocolConstants.CLIENT_CONNECT_TIMEOUT, transportContext.getParameter(ProtocolConstants.RPC_CLIENT_CONNECT_TIMEOUT));
        return new RpcClient(transportContext, handler);
    }
}
