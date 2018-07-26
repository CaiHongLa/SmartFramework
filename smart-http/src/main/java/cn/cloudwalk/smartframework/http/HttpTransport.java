package cn.cloudwalk.smartframework.http;

import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.Client;
import cn.cloudwalk.smartframework.transport.Server;
import cn.cloudwalk.smartframework.transport.Transport;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;

/**
 * HttpTransport
 *
 * @author LIYANHUI
 * @date 2018/1/31
 * @since 1.0.0
 */
public class HttpTransport implements Transport {

    @Override
    public Server bind(TransportContext transportContext, ChannelHandler handler) {
        transportContext = transportContext.addParameter(ProtocolConstants.SERVER_ACCEPTS, transportContext.getParameter(ProtocolConstants.HTTP_SERVER_ACCEPTS));
        transportContext = transportContext.addParameter(ProtocolConstants.DISRUPTOR_CONSUMER_POOL_NAME, "http_channel_disruptor_consumer_pool");
        return new HttpServer(transportContext, handler);
    }

    @Override
    public Client connect(TransportContext transportContext, ChannelHandler handler) {
        throw new UnsupportedOperationException("HttpClient不支持！");
    }
}
