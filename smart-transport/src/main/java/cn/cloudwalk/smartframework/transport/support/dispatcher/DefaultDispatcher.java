package cn.cloudwalk.smartframework.transport.support.dispatcher;

import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.Dispatcher;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;

/**
 * DefaultDispatcher 默认的handler分发器
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class DefaultDispatcher implements Dispatcher {

    @Override
    public ChannelHandler dispatch(TransportContext transportContext, ChannelHandler handler) {
        return new DefaultChannelHandler(transportContext, handler);
    }
}
