package cn.cloudwalk.smartframework.rpc.netty.http;

import cn.cloudwalk.smartframework.transport.AbstractProtocol;
import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.Codec;
import cn.cloudwalk.smartframework.transport.Server;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeHandler;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeServer;
import cn.cloudwalk.smartframework.transport.exchange.support.Exchangers;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * Rpc Http协议
 *
 * @author LIYANHUI
 * @since 2.0.0
 */
public class RpcHttpProtocol extends AbstractProtocol {

    private Server server;
    private TransportContext transportContext;
    private ExchangeHandler requestHandler;

    public RpcHttpProtocol(TransportContext transportContext, ExchangeHandler requestHandler) {
        this.transportContext = transportContext;
        this.transportContext = this.transportContext
                .addParameter(ProtocolConstants.EXCHANGE_HEART_BEAT_TIME, this.transportContext.getParameter(ProtocolConstants.RPC_HTTP_EXCHANGE_HEART_BEAT_TIME))
                .addParameter(ProtocolConstants.EXCHANGE_HEART_BEAT_TIMEOUT, this.transportContext.getParameter(ProtocolConstants.RPC_HTTP_EXCHANGE_HEART_BEAT_TIMEOUT))
                .addParameter(ProtocolConstants.FIXED_THREAD_POOL_CORE_SIZE, this.transportContext.getParameter(ProtocolConstants.RPC_HTTP_FIXED_THREAD_POOL_CORE_SIZE))
                .addParameter(ProtocolConstants.FIXED_THREAD_POOL_QUEUE_SIZE, this.transportContext.getParameter(ProtocolConstants.RPC_HTTP_FIXED_THREAD_POOL_QUEUE_SIZE))
                .addParameter(ProtocolConstants.DISRUPTOR_SWITCH, this.transportContext.getParameter(ProtocolConstants.RPC_DISRUPTOR_SWITCH));
        this.requestHandler = requestHandler;
    }

    @Override
    public int getDefaultPort() {
        return transportContext.getPort();
    }

    @Override
    public void bind() {
        if (server == null) {
            this.server = createNettyServer(transportContext);
        } else {
            server.reset(transportContext);
        }
    }

    private ExchangeServer createNettyServer(TransportContext transportContext) {
        return Exchangers.bind(transportContext, requestHandler);
    }

    @Override
    public Codec getChannelCodec() {
        return transportContext.getCodec();
    }

    @Override
    public Server getServer() {
        return server;
    }

    /**
     * Http服务不用自定义编解码，使用Netty提供的
     */
    public static class HttpCodec implements Codec {

        @Override
        public void decode(Channel channel, ByteBuf byteBuf, List<Object> list) throws Exception {
            //Nothing
        }

        @Override
        public void encode(Channel channel, ByteBuf byteBuf, Object o) throws Exception {
            //Nothing
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (server != null) {
            server.close();
        }
    }
}
