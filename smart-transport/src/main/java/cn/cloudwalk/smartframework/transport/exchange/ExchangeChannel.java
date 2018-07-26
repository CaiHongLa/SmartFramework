package cn.cloudwalk.smartframework.transport.exchange;

import cn.cloudwalk.smartframework.transport.Channel;

/**
 * 数据交换连接
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface ExchangeChannel extends Channel {

    /**
     * 获取连接Handler
     *
     * @return ExchangeHandler
     */
    ExchangeHandler getExchangeHandler();

}