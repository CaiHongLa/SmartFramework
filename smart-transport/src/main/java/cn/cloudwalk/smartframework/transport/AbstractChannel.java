package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;

/**
 * AbstractChannel
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public abstract class AbstractChannel extends AbstractPeer implements Channel {

    public AbstractChannel(TransportContext transportContext, ChannelHandler handler) {
        super(transportContext, handler);
    }

    @Override
    public void send(Object message, boolean sent) throws TransportException {
        if (isClosed()) {
            throw new IllegalStateException("The service has been closed and no message can be sent :" + getRemoteAddress());
        }
    }

    @Override
    public String toString() {
        return getLocalAddress() + " -> " + getRemoteAddress();
    }
}
