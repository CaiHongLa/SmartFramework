package cn.cloudwalk.smartframework.transport;

import cn.cloudwalk.smartframework.transport.support.transport.TransportException;

/**
 * 协议接口
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface Protocol {

    /**
     * 协议接口 对于服务器而言是服务器的监听端口，对于客户端是需要连接的服务的端口
     *
     * @return 端口号
     */
    int getDefaultPort();

    /**
     * 发布协议
     *
     * @throws TransportException
     */
    void bind() throws TransportException;

    /**
     * 注销协议
     */
    void destroy();

    /**
     * 获取协议的编解码实现
     *
     * @return Codec
     */
    Codec getChannelCodec();

    /**
     * 获取服务实例
     *
     * @return Server
     */
    Server getServer();

}
