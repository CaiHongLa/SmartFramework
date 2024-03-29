package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.transportcomponents.AbstractChannel;
import cn.cloudwalk.smartframework.transportcomponents.ChannelHandler;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 传输层Rpc连接
 *
 * @author LIYANHUI
 * @since 2.0.10
 */
public class RpcChannel extends AbstractChannel {

    private static final Logger logger = LogManager.getLogger(RpcChannel.class);

    private static final ConcurrentMap<Channel, RpcChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private final Channel channel;

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    private RpcChannel(TransportContext transportContext, Channel channel, ChannelHandler handler) {
        super(transportContext, handler);
        this.channel = channel;
    }

    public static RpcChannel getOrAddChannel(TransportContext transportContext, Channel ch, ChannelHandler handler) {
        if (ch == null) {
            return null;
        }
        RpcChannel ret = CHANNEL_MAP.get(ch);
        if (ret == null) {
            RpcChannel rpcChannel = new RpcChannel(transportContext, ch, handler);
            if (ch.isActive()) {
                ret = CHANNEL_MAP.putIfAbsent(ch, rpcChannel);
            }
            if (ret == null) {
                ret = rpcChannel;
            }
        }
        return ret;
    }

    public static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isActive()) {
            CHANNEL_MAP.remove(ch);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public boolean isConnected() {
        return !isClosed() && channel.isActive();
    }

    @Override
    public void send(Object message, boolean sent) throws TransportException {
        super.send(message, sent);

        boolean success = true;
        int timeout = 0;
        try {
            ChannelFuture future = channel.writeAndFlush(message);
            if (sent) {
                timeout = 10 * 1000;
                success = future.await(timeout);
            }
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            throw new TransportException(this, "send message：" + message + " to " + getRemoteAddress() + ", error: " + e.getMessage(), e);
        }

        if (!success) {
            throw new TransportException(this, "send message：" + message + " to " + getRemoteAddress()
                    + "，timeout：" + timeout + "ms");
        }
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            removeChannelIfDisconnected(channel);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            attributes.clear();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            logger.info("close channel：" + channel);
            channel.close();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public void reset(TransportContext transportContext) {

    }

    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        if (value == null) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
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
        RpcChannel other = (RpcChannel) obj;
        if (channel == null) {
            return other.channel == null;
        } else return channel.equals(other.channel);
    }

    @Override
    public String toString() {
        return "RpcChannel [channel=" + channel + "]";
    }
}
