package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.transport.support.transport.TransportException;

/**
 * 客户端
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface Client extends Channel, EndPoint {

    /**
     * 重连
     *
     * @throws TransportException
     */
    void reconnect() throws TransportException;
}
