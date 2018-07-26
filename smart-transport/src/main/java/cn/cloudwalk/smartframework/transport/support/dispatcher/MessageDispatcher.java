package cn.cloudwalk.smartframework.transport.support.dispatcher;

import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.Dispatcher;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;

/**
 * 只将接收消息分发到业务线程池，其余在IO线程处理
 *
 * @author LIYANHUI
 * @date 2018/1/14
 * @since 1.0.0
 */
public class MessageDispatcher implements Dispatcher {

    @Override
    public ChannelHandler dispatch(TransportContext transportContext, ChannelHandler handler) {
        return new MessageChannelHandler(transportContext, handler);
    }
}
