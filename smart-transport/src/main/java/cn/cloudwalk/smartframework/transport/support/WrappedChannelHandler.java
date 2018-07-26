package cn.cloudwalk.smartframework.transport.support;


import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.ChannelHandlerDelegate;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * WrappedChannelHandler
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class WrappedChannelHandler implements ChannelHandlerDelegate {

    protected static final Logger logger = LogManager.getLogger(WrappedChannelHandler.class);

    protected static final ExecutorService SHARED_EXECUTOR = new ThreadPoolExecutor(100, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(65536), new ThreadFactoryBuilder()
            .setNameFormat("Shared-pool").build(), new ThreadPoolExecutor.AbortPolicy());


    protected final ExecutorService executor;

    protected final ChannelHandler handler;

    private TransportContext transportContext;


    public WrappedChannelHandler(TransportContext transportContext, ChannelHandler handler) {
        this.handler = handler;
        this.transportContext = transportContext;
        executor = (ExecutorService) transportContext.getThreadPool().newExecutor(transportContext);
    }

    public void close() {
        try {
            if (executor != null) {
                executor.shutdown();
            }
        } catch (Throwable t) {
            logger.warn("关闭线程池失败: " + t.getMessage(), t);
        }
    }

    @Override
    public void connected(Channel channel) throws TransportException {
        handler.connected(channel);
    }

    @Override
    public void disconnected(Channel channel) throws TransportException {
        handler.disconnected(channel);
    }

    @Override
    public void send(Channel channel, Object message) throws TransportException {
        handler.send(channel, message);
    }

    @Override
    public void received(Channel channel, Object message) throws TransportException {
        handler.received(channel, message);
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws TransportException {
        handler.caught(channel, exception);
    }

    @Override
    public ChannelHandler getHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate) handler).getHandler();
        } else {
            return handler;
        }
    }

    public TransportContext getTransportContext() {
        return transportContext;
    }

}
