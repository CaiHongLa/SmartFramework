package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponse;
import cn.cloudwalk.smartframework.rpc.client.codec.SerializationUtil;
import cn.cloudwalk.smartframework.transport.*;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeHandler;
import cn.cloudwalk.smartframework.transport.exchange.support.Exchangers;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * Rpc服务协议
 *
 * @author LIYANHUI
 * @since 2.0.10
 */
public class RpcProtocol extends AbstractProtocol {

    private Client client;
    private Server server;
    private TransportContext transportContext;
    private ExchangeHandler requestHandler;

    public RpcProtocol(TransportContext transportContext, ExchangeHandler requestHandler) {
        this.transportContext = transportContext;
        this.transportContext = this.transportContext
                .addParameter(ProtocolConstants.EXCHANGE_HEART_BEAT_TIME, this.transportContext.getParameter(ProtocolConstants.RPC_EXCHANGE_HEARTBEAT_TIME))
                .addParameter(ProtocolConstants.EXCHANGE_HEART_BEAT_TIMEOUT, this.transportContext.getParameter(ProtocolConstants.RPC_EXCHANGE_HEARTBEAT_TIMEOUT))
                .addParameter(ProtocolConstants.FIXED_THREAD_POOL_CORE_SIZE, this.transportContext.getParameter(ProtocolConstants.RPC_FIXED_THREAD_CORE_SIZE))
                .addParameter(ProtocolConstants.FIXED_THREAD_POOL_QUEUE_SIZE, this.transportContext.getParameter(ProtocolConstants.RPC_FIXED_THREAD_QUEUE_SIZE))
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
            server = Exchangers.bind(transportContext, requestHandler);
        } else {
            server.reset(transportContext);
        }

    }

    public Client getClient() {
        return client;
    }


    public void connect() {
        if (client == null) {
            client = Exchangers.connect(transportContext, requestHandler);
        } else {
            client.reset(transportContext);
        }
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public Codec getChannelCodec() {
        return transportContext.getCodec();
    }

    public static class NettyClientCodec implements Codec {

        @Override
        public void decode(Channel channel, ByteBuf in, List<Object> out) {
            if (in.readableBytes() < 4) {
                return;
            }
            in.markReaderIndex();
            int dataLength = in.readInt();
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] data = new byte[dataLength];
            in.readBytes(data);

            NettyRpcResponse response = SerializationUtil.deserialize(data, NettyRpcResponse.class);
            out.add(response);
        }

        @Override
        public void encode(Channel channel, ByteBuf byteBuf, Object msg) {
            byte[] data = SerializationUtil.serialize(msg);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }

    public static class NettyServerCodec implements Codec {

        @Override
        public void decode(Channel channel, ByteBuf in, List<Object> out) {
            if (in.readableBytes() < 4) {
                return;
            }
            in.markReaderIndex();
            int dataLength = in.readInt();
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] data = new byte[dataLength];
            in.readBytes(data);

            NettyRpcRequest request = SerializationUtil.deserialize(data, NettyRpcRequest.class);
            out.add(request);
        }

        @Override
        public void encode(Channel channel, ByteBuf byteBuf, Object msg) {
            byte[] data = SerializationUtil.serialize(msg);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (client != null) {
            client.close();
        }
        if(server != null){
            server.close();
        }
    }
}
