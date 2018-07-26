package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.transport.support.ChannelHandlers;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * AbstractClient
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public abstract class AbstractClient extends AbstractEndpoint implements Client {
    private static final Logger logger = LogManager.getLogger(AbstractClient.class);
    private final Lock connectLock = new ReentrantLock();

    public AbstractClient(TransportContext transportContext, ChannelHandler handler) {
        super(transportContext, handler);
        try {
            doOpen();
        } catch (Throwable t) {
            logger.error("打开连接异常", t);
            close();
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(t));
        }
        try {
            connect();
        } catch (Exception t) {
            close();
            throw t;
        }
    }

    protected static ChannelHandler wrapChannelHandler(TransportContext transportContext, ChannelHandler handler) {
        return ChannelHandlers.wrapClient(transportContext, handler);
    }


    protected InetSocketAddress getConnectAddress() {
        return new InetSocketAddress(getTransportContext().getHost(), getTransportContext().getPort());
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        Channel channel = getChannel();
        if (channel == null) {
            return new InetSocketAddress(getTransportContext().getHost(), getTransportContext().getPort());
        }
        return channel.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        Channel channel = getChannel();
        if (channel == null) {
            return null;
        }
        return channel.getLocalAddress();
    }

    @Override
    public Object getAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null) {
            return null;
        }
        return channel.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        Channel channel = getChannel();
        if (channel == null) {
            return;
        }
        channel.setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        Channel channel = getChannel();
        if (channel == null) {
            return;
        }
        channel.removeAttribute(key);
    }

    @Override
    public boolean hasAttribute(String key) {
        Channel channel = getChannel();
        return channel != null && channel.hasAttribute(key);
    }

    @Override
    public void send(Object message, boolean sent) throws TransportException {
        if (!isConnected()) {
            connect();
        }
        Channel channel = getChannel();
        if (channel == null || !channel.isConnected()) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("连接已经关闭，不能发送消息"));
        }
        channel.send(message, sent);
    }

    protected void connect() {
        connectLock.lock();
        try {
            if (isConnected()) {
                return;
            }
            doConnect();
            if (!isConnected()) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("与服务器建立连接失败"));
            }
        } catch (Throwable e) {
            logger.error("与服务器建立连接异常，" + TextUtil.getStackTrace(e));
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("与服务器建立连接异常", e));
        } finally {
            connectLock.unlock();
        }
    }

    @Override
    public boolean isConnected() {
        Channel channel = getChannel();
        return channel != null && channel.isConnected();
    }

    private void disconnect() {
        connectLock.lock();
        try {
            try {
                Channel channel = getChannel();
                if (channel != null) {
                    channel.close();
                }
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
            try {
                doDisConnect();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        } finally {
            connectLock.unlock();
        }
    }

    @Override
    public void reconnect() {
        disconnect();
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            disconnect();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            doClose();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

    }

    @Override
    public void reset(TransportContext transportContext) {
    }

    protected abstract void doOpen() throws Throwable;

    protected abstract void doClose() throws Throwable;

    protected abstract void doConnect() throws Throwable;

    protected abstract void doDisConnect() throws Throwable;

    protected abstract Channel getChannel();

}
