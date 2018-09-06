package cn.cloudwalk.smartframework.transport.exchange.support.header;

import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.ChannelHandlerDelegate;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeChannel;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeHandler;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * HeaderExchangeChannel
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
final class HeaderExchangeChannel implements ExchangeChannel {

    private static final Logger logger = LogManager.getLogger(HeaderExchangeChannel.class);

    private static final String CHANNEL_KEY = HeaderExchangeChannel.class.getName() + ".CHANNEL";

    private final Channel channel;

    private volatile boolean closed = false;

    HeaderExchangeChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("channel can not be null！");
        }
        this.channel = channel;
    }

    static HeaderExchangeChannel getOrAddChannel(Channel ch) {
        if (ch == null) {
            return null;
        }
        HeaderExchangeChannel ret = (HeaderExchangeChannel) ch.getAttribute(CHANNEL_KEY);
        if (ret == null) {
            ret = new HeaderExchangeChannel(ch);
            if (ch.isConnected()) {
                ch.setAttribute(CHANNEL_KEY, ret);
            }
        }
        return ret;
    }

    static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isConnected()) {
            ch.removeAttribute(CHANNEL_KEY);
        }
    }

    @Override
    public void send(Object message) throws TransportException {
        send(message, false);

    }

    @Override
    public void send(Object message, boolean sent) throws TransportException {
        if (closed) {
            throw new TransportException(this.getLocalAddress(), null, "send message：" + message + " failed，" + this + " is closed！");
        }
        channel.send(message, sent);
    }


    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void reset(TransportContext transportContext) {

    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public void startClose() {
        channel.startClose();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return channel.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    @Override
    public TransportContext getTransportContext() {
        return channel.getTransportContext();
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channel.getChannelHandler();
    }

    @Override
    public ExchangeHandler getExchangeHandler() {
        return (ExchangeHandler) channel.getChannelHandler();
    }

    @Override
    public ChannelHandlerDelegate getChannelHandlerDelegate() {
        return channel.getChannelHandlerDelegate();
    }

    @Override
    public Object getAttribute(String key) {
        return channel.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        channel.setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        channel.removeAttribute(key);
    }

    @Override
    public boolean hasAttribute(String key) {
        return channel.hasAttribute(key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + channel.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        HeaderExchangeChannel other = (HeaderExchangeChannel) obj;
        return channel.equals(other.channel);
    }

    @Override
    public String toString() {
        return channel.toString();
    }

}