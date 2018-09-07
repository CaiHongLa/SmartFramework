package cn.cloudwalk.smartframework.transport.support.dispatcher;

import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.support.ChannelEventRunnable;
import cn.cloudwalk.smartframework.transport.support.WrappedChannelHandler;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * DefaultChannelHandler
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class DefaultChannelHandler extends WrappedChannelHandler {

    public DefaultChannelHandler(TransportContext transportContext, ChannelHandler handler) {
        super(transportContext, handler);
    }

    @Override
    public void connected(Channel channel) throws TransportException {
        ExecutorService executor = getExecutorService();
        try {
            executor.execute(new ChannelEventRunnable(channel, handler, ChannelEventRunnable.ChannelState.CONNECTED));
        } catch (Throwable t) {
            throw new TransportException(channel, getClass() + " handling connection setup exception", t);
        }
    }

    @Override
    public void disconnected(Channel channel) throws TransportException {
        ExecutorService executor = getExecutorService();
        try {
            executor.execute(new ChannelEventRunnable(channel, handler, ChannelEventRunnable.ChannelState.DISCONNECTED));
        } catch (Throwable t) {
            throw new TransportException(channel, getClass() + " handling exceptions during connection disconnection", t);
        }
    }

    @Override
    public void received(Channel channel, Object message) throws TransportException {
        ExecutorService executor = getExecutorService();
        try {
            executor.execute(new ChannelEventRunnable(channel, handler, ChannelEventRunnable.ChannelState.RECEIVED, message));
        } catch (Throwable t) {
            if (t instanceof RejectedExecutionException) {
                return;
            }
            throw new TransportException(channel, getClass() + " exception handling message", t);
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws TransportException {
        ExecutorService executor = getExecutorService();
        try {
            executor.execute(new ChannelEventRunnable(channel, handler, ChannelEventRunnable.ChannelState.CAUGHT, exception));
        } catch (Throwable t) {
            throw new TransportException(channel, getClass() + " exception handling exception", t);
        }
    }
}
