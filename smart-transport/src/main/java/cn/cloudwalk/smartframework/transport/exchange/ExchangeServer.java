package cn.cloudwalk.smartframework.transport.exchange;

import cn.cloudwalk.smartframework.transport.Server;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * 数据交换服务
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface ExchangeServer extends Server {

    /**
     * 获取服务建立的连接
     *
     * @return ExchangeChannel集合
     */
    Collection<ExchangeChannel> getExchangeChannels();

    /**
     * 获取某个地址的连接
     *
     * @param remoteAddress 对端地址
     * @return ExchangeChannel
     */
    ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress);
}