package cn.cloudwalk.smartframework.transport.support.disruptor;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.EventFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 传输连接事件
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/4/28 10:59
 * @since 2.0.0
 */
public class ChannelEvent {

    private static final Logger logger = LogManager.getLogger(ChannelEvent.class);
    public static final Factory FACTORY = new Factory();
    private Channel channel;
    private ChannelState state;
    private Throwable exception;
    private Object message;
    private transient ChannelHandler handler;
    protected static final ExecutorService SHARED_EXECUTOR = new ThreadPoolExecutor(100, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(65536), new ThreadFactoryBuilder()
            .setNameFormat("Shared-pool").build(), new ThreadPoolExecutor.AbortPolicy());

    public ChannelEvent() {
    }

    public void setEventValues(ChannelHandler handler, Channel channel, ChannelState state, Throwable throwable, Object message) {
        this.channel = channel;
        this.state = state;
        this.exception = throwable;
        this.message = message;
        this.handler = handler;
    }

    public void executeEvent() throws TransportException {
        if (null != this.state) {
            switch (this.state) {
                case CONNECTED:
                    this.handler.connected(this.channel);
                    break;
                case DISCONNECTED:
                    this.handler.disconnected(this.channel);
                    break;
                case SENT:
                    this.handler.send(this.channel, this.message);
                    break;
                case RECEIVED:
//                    ChannelHandler handlerCOpy = handler;
//                    SHARED_EXECUTOR.execute(new ChannelEventRunnable(channel, handlerCOpy, ChannelEventRunnable.ChannelState.RECEIVED, message));
                    this.handler.received(this.channel, this.message);
                    break;
                case CAUGHT:
                    this.handler.caught(this.channel, this.exception);
                    break;
                default:
                    break;
            }
        }
    }

    public void executeException(Throwable ex, long sequence, ChannelEvent event){
        if (ex instanceof TransportException) {
            logger.info("Connection occurred transmission exception：" + ex + ",Close to close！");
            channel.close();
            return;
        }
        throw new FrameworkInternalSystemException(new SystemExceptionDesc(ex));
    }

    public void clear() {
        this.setEventValues(null, null, null, null, null);
    }

    @Override
    public String toString() {
        return "ChannelEvent{" +
                "channel=" + channel +
                ", state=" + state +
                ", exception=" + exception +
                ", message=" + message +
                "} ";
    }

    private static class Factory implements EventFactory<ChannelEvent> {
        private Factory() {
        }

        @Override
        public ChannelEvent newInstance() {
            return new ChannelEvent();
        }
    }

    public enum ChannelState {

        CONNECTED,

        DISCONNECTED,

        SENT,

        RECEIVED,

        CAUGHT
    }

}
