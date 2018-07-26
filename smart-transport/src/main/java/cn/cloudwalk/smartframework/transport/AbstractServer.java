package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * AbstractServer
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public abstract class AbstractServer extends AbstractEndpoint implements Server {

    private static final Logger logger = LogManager.getLogger(AbstractServer.class);
    private InetSocketAddress bindAddress;
    private InetSocketAddress localAddress;
    private int accepts;

    public AbstractServer(TransportContext transportContext, ChannelHandler handler) {
        super(transportContext, handler);
        String bindIp = transportContext.getHost();
        int bindPort = transportContext.getPort();
        bindAddress = new InetSocketAddress(bindIp, bindPort);
        localAddress = bindAddress;
        accepts = transportContext.getParameter(ProtocolConstants.SERVER_ACCEPTS, 1000);
        try {
            doOpen();
            logger.info(getClass().getSimpleName() + "服务在地址: " + getBindAddress() + "启动完成！");
        } catch (Throwable throwable) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(getClass().getSimpleName() + "服务在地址: " +
                    getBindAddress() + "启动失败, 原因: " + throwable.getMessage()));
        }
    }

    protected abstract void doOpen() throws Throwable;

    protected abstract void doClose() throws Throwable;

    @Override
    public void reset(TransportContext transportContext) {
    }

    @Override
    public void disconnected(Channel channel) throws TransportException {
        Collection<Channel> channels = getChannels();
        if (channels.size() == 0) {
            logger.warn("所有连接已经从服务：" + getBindAddress() + "断开。服务器现在可以停止了！");
        }
        super.disconnected(channel);
    }

    @Override
    public void connected(Channel channel) throws TransportException {
        if (this.isClosing() || this.isClosed()) {
            logger.warn("服务器" + getClass().getSimpleName() + "/" + getBindAddress() + "处于正在关闭或已关闭状态，不再接受新的连接: " + channel);
            channel.close();
            return;
        }

        Collection<Channel> channels = getChannels();
        if (accepts > 0 && channels.size() > accepts) {
            logger.error("服务器" + getClass().getSimpleName() + "/" + getBindAddress() + "已经达到最大连接数：" + accepts + " ,不再接受新的连接: " + channel);
            channel.close();
            return;
        }
        super.connected(channel);
    }

    @Override
    public void send(Object msg, boolean sent) throws TransportException {
        Collection<Channel> channels = getChannels();
        for (Channel channel : channels) {
            if (channel.isConnected()) {
                channel.send(msg, sent);
            }
        }
    }

    @Override
    public void close() {
        logger.info("开始关闭" + getClass().getSimpleName() + " ，地址：" + getBindAddress());
        try {
            super.close();
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
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    protected InetSocketAddress getBindAddress() {
        return bindAddress;
    }


}
