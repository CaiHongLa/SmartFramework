package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.transportcomponents.ChannelHandler;
import cn.cloudwalk.smartframework.transportcomponents.Client;
import cn.cloudwalk.smartframework.transportcomponents.Server;
import cn.cloudwalk.smartframework.transportcomponents.Transport;
import cn.cloudwalk.smartframework.transportcomponents.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportContext;

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
