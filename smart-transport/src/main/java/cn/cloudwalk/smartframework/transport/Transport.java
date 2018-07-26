package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;

/**
 * 传输层对外接口，提供bind和connect方法。
 * <p>
 * bind：建立服务使用
 * <p>
 * connect：连接服务使用
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface Transport {

    /**
     * 建立服务
     *
     * @param transportContext 协议传递过来的上下文
     * @param handler          服务处理Handler
     * @return Server
     */
    Server bind(TransportContext transportContext, ChannelHandler handler);

    /**
     * 连接到服务
     *
     * @param transportContext 协议传递过来的上下文
     * @param handler          处理连接的Handler
     * @return Client
     */
    Client connect(TransportContext transportContext, ChannelHandler handler);
}
