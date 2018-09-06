package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;

/**
 * AbstractPeer
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public abstract class AbstractPeer implements EndPoint, ChannelHandler {

    private final ChannelHandler handler;
    private TransportContext transportContext;
    private volatile boolean closing;
    private volatile boolean closed;

    AbstractPeer(TransportContext transportContext, ChannelHandler handler) {
        if (transportContext == null) {
            throw new IllegalArgumentException("transportContext can not be null！");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler can not be null!");
        }

        this.transportContext = transportContext;
        this.handler = handler;
    }

    @Override
    public void send(Object msg) throws TransportException {
        send(msg, false);
    }

    @Override
    public TransportContext getTransportContext() {
        return transportContext;
    }

    protected void setTransportContext(TransportContext transportContext) {
        if (transportContext == null) {
            throw new IllegalArgumentException("transportContext can not be null！");
        }
        this.transportContext = transportContext;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public void startClose() {
        if (isClosed()) {
            return;
        }
        closing = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public ChannelHandler getChannelHandler() {
        if (handler instanceof ChannelHandlerDelegate) {
            return ((ChannelHandlerDelegate) handler).getHandler();
        }
        return handler;
    }

    @Override
    public ChannelHandlerDelegate getChannelHandlerDelegate() {
        if(handler instanceof ChannelHandlerDelegate){
            return (ChannelHandlerDelegate) handler;
        }
        return null;
    }

    boolean isClosing() {
        return closing && !closed;
    }

    @Override
    public void connected(Channel ch) throws TransportException {
        if (closed) {
            return;
        }
        handler.connected(ch);
    }

    @Override
    public void disconnected(Channel ch) throws TransportException {
        handler.disconnected(ch);
    }

    @Override
    public void send(Channel ch, Object msg) throws TransportException {
        if (closed) {
            return;
        }
        handler.send(ch, msg);
    }

    @Override
    public void received(Channel ch, Object msg) throws TransportException {
        if (closed) {
            return;
        }
        handler.received(ch, msg);
    }

    @Override
    public void caught(Channel ch, Throwable ex) throws TransportException {
        handler.caught(ch, ex);
    }
}