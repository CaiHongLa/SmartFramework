package cn.cloudwalk.smartframework.transport.support;

import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.exchange.support.header.HeartbeatHandler;
import cn.cloudwalk.smartframework.transport.support.disruptor.ChannelDisruptorHandler;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;

/**
 * ChannelHandlers
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class ChannelHandlers {

    private static ChannelHandlers INSTANCE = new ChannelHandlers();

    private ChannelHandlers() {
    }

    private static ChannelHandlers getInstance() {
        return INSTANCE;
    }

    /**
     * 服务端线程模型包装
     *
     * @param transportContext
     * @param handler
     * @return
     */
    public static ChannelHandler wrapServer(TransportContext transportContext, ChannelHandler handler) {
        return ChannelHandlers.getInstance().wrapServerInternal(transportContext, handler);
    }

    /**
     * 客户端线程模型包装
     *
     * @param transportContext
     * @param handler
     * @return
     */
    public static ChannelHandler wrapClient(TransportContext transportContext, ChannelHandler handler) {
        return ChannelHandlers.getInstance().wrapClientInternal(transportContext, handler);
    }

    /**
     * 使用Disruptor队列包装服务端Handler
     *
     * @param transportContext
     * @param handler
     * @return
     */
    private ChannelHandler wrapServerInternal(TransportContext transportContext, ChannelHandler handler) {
        return new HeartbeatHandler(new ChannelDisruptorHandler(transportContext, handler));
    }

    /**
     * 使用Dispatcher分发器包装客户端Handler
     *
     * @param transportContext
     * @param handler
     * @return
     */
    private ChannelHandler wrapClientInternal(TransportContext transportContext, ChannelHandler handler) {
        return new HeartbeatHandler(transportContext.getDispatcher().dispatch(transportContext, handler));
    }
}
