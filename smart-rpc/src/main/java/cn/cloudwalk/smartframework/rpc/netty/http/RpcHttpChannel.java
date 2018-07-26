package cn.cloudwalk.smartframework.rpc.netty.http;

import cn.cloudwalk.smartframework.transport.AbstractChannel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * http rpc channel
 *
 * @author LIYANHUI
 * @since 2.0.0
 */
public class RpcHttpChannel extends AbstractChannel {

    private static final Logger logger = LogManager.getLogger(RpcHttpChannel.class);

    private static final ConcurrentMap<Channel, RpcHttpChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private final Channel channel;

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    private RpcHttpChannel(TransportContext transportContext, Channel channel, ChannelHandler handler) {
        super(transportContext, handler);
        this.channel = channel;
    }

    static RpcHttpChannel getOrAddChannel(TransportContext transportContext, Channel ch, ChannelHandler handler) {
        if (ch == null) {
            return null;
        }
        RpcHttpChannel ret = CHANNEL_MAP.get(ch);
        if (ret == null) {
            RpcHttpChannel rpcHttpChannel = new RpcHttpChannel(transportContext, ch, handler);
            if (ch.isActive()) {
                ret = CHANNEL_MAP.putIfAbsent(ch, rpcHttpChannel);
            }
            if (ret == null) {
                ret = rpcHttpChannel;
            }
        }
        return ret;
    }

    static void removeChannelIfDisconnected(Channel ch) {
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
            throw new TransportException(this, "发送消息：" + message + " 到 " + getRemoteAddress() + ", 异常: " + e.getMessage(), e);
        }

        if (!success) {
            throw new IllegalStateException("发现消息：" + message + " 到 " + getRemoteAddress()
                    + "，超时：" + timeout + "ms");
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
            logger.info("关闭连接：" + channel);
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
        RpcHttpChannel other = (RpcHttpChannel) obj;
        if (channel == null) {
            if (other.channel != null) {
                return false;
            }
        } else if (!channel.equals(other.channel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RpcHttpChannel [channel=" + channel + "]";
    }
}
