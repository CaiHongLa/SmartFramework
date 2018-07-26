package cn.cloudwalk.smartframework.transport;


import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;

/**
 * Handler分发器，搭配Handler使用
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface Dispatcher {

    /**
     * 获取分发器对应的Handler
     *
     * @param transportContext 协议上下文
     * @param handler          原始Handler
     * @return 协议对应的Handler
     */
    ChannelHandler dispatch(TransportContext transportContext, ChannelHandler handler);
}
