package cn.cloudwalk.smartframework.transport.support.disruptor;

import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import com.lmax.disruptor.EventTranslator;

/**
 * 传输事件转换器
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/4/28 16:55
 * @since 2.0.0
 */
public class ChannelEventTranslator implements EventTranslator<ChannelEvent> {

    private Channel channel;
    private ChannelEvent.ChannelState state;
    private Throwable exception;
    private Object message;
    private ChannelHandler handler;

    ChannelEventTranslator() {
    }

    @Override
    public void translateTo(ChannelEvent event, long sequence) {
        event.setEventValues(this.handler, this.channel, this.state, this.exception, this.message);
        this.clear();
    }

    private void clear(){
        this.setEventValues(null, null, null, null, null);
    }

    public void setEventValues(ChannelHandler handler, Channel channel, ChannelEvent.ChannelState state, Throwable throwable, Object message){
        this.channel = channel;
        this.state = state;
        this.exception = throwable;
        this.message = message;
        this.handler = handler;
    }
}
