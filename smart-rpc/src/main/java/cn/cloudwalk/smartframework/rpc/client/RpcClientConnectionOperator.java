package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.clientcomponents.core.ClientConnectionOperator;
import cn.cloudwalk.smartframework.clientcomponents.core.ManagedClient;
import cn.cloudwalk.smartframework.clientcomponents.core.ManagedClientConnection;
import cn.cloudwalk.smartframework.clientcomponents.core.Route;
import cn.cloudwalk.smartframework.clientcomponents.core.config.RequestConfig;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponse;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;
import cn.cloudwalk.smartframework.rpc.invoke.FutureSet;
import cn.cloudwalk.smartframework.transportcomponents.Channel;
import cn.cloudwalk.smartframework.transportcomponents.Client;
import cn.cloudwalk.smartframework.transportcomponents.exchange.ExchangeHandler;
import cn.cloudwalk.smartframework.transportcomponents.exchange.support.ExchangeHandlerAdapter;
import cn.cloudwalk.smartframework.transportcomponents.support.dispatcher.MessageDispatcher;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Rpc连接器
 *
 * @since 2.0.10
 */
public class RpcClientConnectionOperator implements ClientConnectionOperator {

    @Override
    public void connect(ManagedClientConnection conn, Route route, RequestConfig requestConfig) throws IOException {
        Map<String, String> parameters = requestConfig.getParams();
        TransportContext transportContext = new TransportContext(route.getHostIp(), route.getHostPort(), parameters, new RpcProtocol.NettyClientCodec(), new RpcTransport(), new RpcThreadPool(), new MessageDispatcher());
        RpcProtocol protocol = new RpcProtocol(transportContext, requestHandler);
        protocol.connect();
        Client client = protocol.getClient();
        ManagedClient client0 = new ManagedRpcClient(client);
        conn.bind(client0);
    }

    /**
     * 消息处理
     */
    private final ExchangeHandler requestHandler = new ExchangeHandlerAdapter() {

        private final Logger logger = LogManager.getLogger(ExchangeHandler.class);

        @Override
        public void connected(Channel channel) {
        }

        @Override
        public void disconnected(Channel channel) {
        }

        @Override
        public void send(Channel channel, Object message) {

        }

        @Override
        public void received(Channel channel, Object message) {
            NettyRpcResponse response = (NettyRpcResponse) message;
            logger.info("request result：" + response);
            String requestId = response.getRequestId();
            NettyRpcResponseFuture future = FutureSet.futureMap.get(requestId);
            if (future != null) {
                FutureSet.futureMap.remove(requestId);
                future.done(response);
            }
        }

        @Override
        public void caught(Channel channel, Throwable throwable) {
            if (throwable instanceof TransportException) {
                channel.close();
            }
        }
    };
}
