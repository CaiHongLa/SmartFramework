package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.transport.support.transport.TransportException;

/**
 * AbstractChannelHandlerDelegate
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public abstract class AbstractChannelHandlerDelegate implements ChannelHandlerDelegate {

    protected ChannelHandler handler;

    protected AbstractChannelHandlerDelegate(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public ChannelHandler getHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate) handler).getHandler();
        }
        return handler;
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
}
