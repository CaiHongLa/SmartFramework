package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.transport.support.transport.TransportException;

import java.io.Closeable;

/**
 * Handler
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface ChannelHandler extends Closeable {

    /**
     * 连接建立时触发
     *
     * @param channel 连接
     * @throws TransportException
     */
    void connected(Channel channel) throws TransportException;

    /**
     * 连接断开时触发
     *
     * @param channel 连接
     * @throws TransportException
     */
    void disconnected(Channel channel) throws TransportException;

    /**
     * 连接发送消息时触发
     *
     * @param channel 连接
     * @param message 发送的消息
     * @throws TransportException
     */
    void send(Channel channel, Object message) throws TransportException;

    /**
     * 连接收到消息时触发
     *
     * @param channel 连接
     * @param message 消息
     * @throws TransportException
     */
    void received(Channel channel, Object message) throws TransportException;

    /**
     * 连接发生异常时触发
     *
     * @param channel   连接
     * @param throwable 异常
     * @throws TransportException
     */
    void caught(Channel channel, Throwable throwable) throws TransportException;

    /**
     * 关闭handler中的资源
     *
     * @since 2.0.10
     */
    void close();

}