package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;

import java.net.InetSocketAddress;

/**
 * 顶级接口EndPoint
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface EndPoint {

    /**
     * 获取协议上下文
     *
     * @return TransportContext
     */
    TransportContext getTransportContext();

    /**
     * 获取Handler
     *
     * @return ChannelHandler
     */
    ChannelHandler getChannelHandler();

    /**
     * 获取代理Handler
     *
     * @return ChannelHandlerDelegate
     * @since 2.0.10
     */
    ChannelHandlerDelegate getChannelHandlerDelegate();

    /**
     * 获取本地地址
     *
     * @return InetSocketAddress
     */
    InetSocketAddress getLocalAddress();

    /**
     * 发送消息
     *
     * @param msg 消息
     * @throws TransportException
     */
    void send(Object msg) throws TransportException;

    /**
     * 发送消息
     *
     * @param msg  消息
     * @param sent 是否等待消息发送成功
     * @throws TransportException
     */
    void send(Object msg, boolean sent) throws TransportException;

    /**
     * 关闭
     */
    void close();

    /**
     * 进入关闭状态，不再接收新连接
     */
    void startClose();

    /**
     * 是否已关闭
     *
     * @return 关闭：true 未关闭：false
     */
    boolean isClosed();

    /**
     * 重置
     *
     * @param transportContext 协议上下文
     */
    void reset(TransportContext transportContext);
}
