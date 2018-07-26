package cn.cloudwalk.smartframework.transport.exchange.support.header;

import cn.cloudwalk.smartframework.transport.AbstractChannelHandlerDelegate;
import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;

/**
 * 心跳处理Handler
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class HeartbeatHandler extends AbstractChannelHandlerDelegate {

    public static String KEY_READ_TIMESTAMP = "READ_TIMESTAMP";

    public static String KEY_WRITE_TIMESTAMP = "WRITE_TIMESTAMP";

    public HeartbeatHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public void connected(Channel channel) throws TransportException {
        setReadTimestamp(channel);
        setWriteTimestamp(channel);
        handler.connected(channel);
    }

    @Override
    public void disconnected(Channel channel) throws TransportException {
        clearReadTimestamp(channel);
        clearWriteTimestamp(channel);
        handler.disconnected(channel);
    }

    @Override
    public void send(Channel channel, Object message) throws TransportException {
        setWriteTimestamp(channel);
        handler.send(channel, message);
    }

    @Override
    public void received(Channel channel, Object message) throws TransportException {
        setReadTimestamp(channel);
        handler.received(channel, message);
    }

    private void setReadTimestamp(Channel channel) {
        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
    }

    private void setWriteTimestamp(Channel channel) {
        channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());
    }

    private void clearReadTimestamp(Channel channel) {
        channel.removeAttribute(KEY_READ_TIMESTAMP);
    }

    private void clearWriteTimestamp(Channel channel) {
        channel.removeAttribute(KEY_WRITE_TIMESTAMP);
    }

}
