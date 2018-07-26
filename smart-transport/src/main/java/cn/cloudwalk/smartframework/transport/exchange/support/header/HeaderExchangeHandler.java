package cn.cloudwalk.smartframework.transport.exchange.support.header;

import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.ChannelHandlerDelegate;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeChannel;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeHandler;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HeaderExchangeHandler
 * <p>
 * 实现DisruptorHandler使其具备Disruptor消费者的能力
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class HeaderExchangeHandler implements ChannelHandlerDelegate {

    protected static final Logger logger = LogManager.getLogger(HeaderExchangeHandler.class);

    public static String KEY_READ_TIMESTAMP = HeartbeatHandler.KEY_READ_TIMESTAMP;

    public static String KEY_WRITE_TIMESTAMP = HeartbeatHandler.KEY_WRITE_TIMESTAMP;

    private final ExchangeHandler handler;

    public HeaderExchangeHandler(ExchangeHandler handler) {
        this.handler = handler;
    }

    @Override
    public void connected(Channel channel) throws TransportException {
        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
        channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());
        ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            handler.connected(exchangeChannel);
        } finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void disconnected(Channel channel) throws TransportException {
        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
        channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());
        ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            handler.disconnected(exchangeChannel);
        } finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void send(Channel channel, Object message) throws TransportException {
        Throwable exception = null;
        try {
            channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());
            ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
            try {
                handler.send(exchangeChannel, message);
            } finally {
                HeaderExchangeChannel.removeChannelIfDisconnected(channel);
            }
        } catch (Throwable t) {
            exception = t;
        }
        if (exception != null) {
            if (exception instanceof RuntimeException) {
                throw (RuntimeException) exception;
            } else if (exception instanceof TransportException) {
                throw (TransportException) exception;
            } else {
                throw new TransportException(channel.getLocalAddress(), channel.getRemoteAddress(),
                        exception.getMessage(), exception);
            }
        }
    }

    @Override
    public void received(Channel channel, Object message) throws TransportException {
        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
        ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            handler.received(exchangeChannel, message);
        } finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws TransportException {
        ExchangeChannel exchangeChannel = HeaderExchangeChannel.getOrAddChannel(channel);
        try {
            handler.caught(exchangeChannel, exception);
        } finally {
            HeaderExchangeChannel.removeChannelIfDisconnected(channel);
        }
    }

    @Override
    public ChannelHandler getHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate) handler).getHandler();
        } else {
            return handler;
        }
    }
}