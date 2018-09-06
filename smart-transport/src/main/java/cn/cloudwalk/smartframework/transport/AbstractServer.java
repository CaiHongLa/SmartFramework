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
            logger.info(getClass().getSimpleName() + " at address: " + getBindAddress() + " start completion！");
        } catch (Throwable throwable) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(getClass().getSimpleName() + " at address: " +
                    getBindAddress() + " start failed, reason: " + throwable.getMessage()));
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
            logger.warn("All connections have been disconnected from services：" + getBindAddress() + ", server can stop now.");
        }
        super.disconnected(channel);
    }

    @Override
    public void connected(Channel channel) throws TransportException {
        if (this.isClosing() || this.isClosed()) {
            logger.warn("Server " + getClass().getSimpleName() + "/" + getBindAddress() + " is on or off, no longer accept new connections: " + channel);
            channel.close();
            return;
        }

        Collection<Channel> channels = getChannels();
        if (accepts > 0 && channels.size() > accepts) {
            logger.error("Server " + getClass().getSimpleName() + "/" + getBindAddress() + " maximum number of connections has been reached：" + accepts + " ,no longer accept new connections: " + channel);
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
        logger.info("Close " + getClass().getSimpleName() + " ，address：" + getBindAddress());
        try {
            super.close();
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        ChannelHandlerDelegate handlerDelegate = getChannelHandlerDelegate();
        if(handlerDelegate != null){
            handlerDelegate.close();
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
