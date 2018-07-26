package cn.cloudwalk.smartframework.transport.support.transport;

import cn.cloudwalk.smartframework.transport.Channel;

import java.net.InetSocketAddress;

/**
 * @author LIYANHUI
 */
public class TransportException extends Exception {

    private InetSocketAddress localAddress;

    private InetSocketAddress remoteAddress;

    public TransportException(Channel channel, String msg) {
        this(channel == null ? null : channel.getLocalAddress(), channel == null ? null : channel.getRemoteAddress(),
                msg);
    }

    public TransportException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message) {
        super(message);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public TransportException(Channel channel, Throwable cause) {
        this(channel == null ? null : channel.getLocalAddress(), channel == null ? null : channel.getRemoteAddress(),
                cause);
    }

    public TransportException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, Throwable cause) {
        super(cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public TransportException(Channel channel, String message, Throwable cause) {
        this(channel == null ? null : channel.getLocalAddress(), channel == null ? null : channel.getRemoteAddress(),
                message, cause);
    }

    public TransportException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message,
                              Throwable cause) {
        super(message, cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }
}