package cn.cloudwalk.smartframework.transport.support.disruptor;

import cn.cloudwalk.smartframework.common.task.NamedThreadFactory;
import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.ChannelHandlerDelegate;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 传输层消息Disruptor无锁队列实现
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/4/28 16:43
 * @since 2.0.0
 */
public class ChannelDisruptorHandler implements ChannelHandlerDelegate {

    private static final Logger logger = LogManager.getLogger(ChannelDisruptorHandler.class);

    /**
     * Disruptor RingBuffer Size (2的N次方)
     */
    private static final int DISRUPTOR_RING_BUFFER_SIZE = 262144;

    /**
     * 异步消息处理的Disruptor
     */
    private volatile Disruptor<ChannelEvent> disruptor;
    private ChannelHandler handler;
    private ExecutorService executor;
    private final ThreadLocal<EventInfo> threadLocalInfo = new ThreadLocal<>();

    /**
     * 是否开启Disruptor
     */
    private boolean disruptorSwitch;

    /**
     * 默认不开启Disruptor
     */
    private static final int DISRUPTOR_SWITCH_OFF = 0;

    /**
     * 开启Disruptor
     */
    private static final int DISRUPTOR_SWITCH_ON = 1;

    public ChannelDisruptorHandler(TransportContext transportContext, ChannelHandler handler) {
        this.handler = handler;
        int disruptorSwitchValue = transportContext.getParameter(ProtocolConstants.DISRUPTOR_SWITCH, DISRUPTOR_SWITCH_OFF);
        this.disruptorSwitch = (disruptorSwitchValue == DISRUPTOR_SWITCH_ON);
        if (disruptorSwitch) {
            ThreadFactory factory = new NamedThreadFactory(transportContext.getParameter(ProtocolConstants.DISRUPTOR_CONSUMER_POOL_NAME, "channel_disruptor_consumer_pool"), false);
            this.executor = Executors.newSingleThreadExecutor(factory);
            initInfoForExecutorThread();
            this.disruptor = new Disruptor<>(
                    ChannelEvent.FACTORY,
                    DISRUPTOR_RING_BUFFER_SIZE,
                    factory,
                    ProducerType.SINGLE,
                    new BlockingWaitStrategy()
            );
            this.disruptor.setDefaultExceptionHandler(new ChannelEventExceptionHandler());
            this.disruptor.handleEventsWith(new ChannelEventHandler());
            this.disruptor.start();
        }
    }

    private void initInfoForExecutorThread() {
        this.executor.submit(() -> {
            EventInfo info = new EventInfo(new ChannelEventTranslator());
            this.threadLocalInfo.set(info);
        });
    }

    @Override
    public void connected(Channel channel) throws TransportException {
        handler.connected(channel);
    }

    @Override
    public void disconnected(Channel channel) throws TransportException {
        handler.disconnected(channel);
    }

    @Override
    public void send(Channel channel, Object message) throws TransportException {
        handler.send(channel, message);
    }

    @Override
    public void received(Channel channel, Object message) throws TransportException {
        if (disruptorSwitch) {
            EventInfo info = threadLocalInfo.get();
            if (info == null) {
                info = new EventInfo(new ChannelEventTranslator());
                threadLocalInfo.set(info);
            }
            Disruptor<ChannelEvent> temp = disruptor;
            if (temp == null) {
                logger.error("The Disruptor queue has been closed, and the message is no longer received.");
            } else if (temp.getRingBuffer().remainingCapacity() == 0L) {
                this.handler.received(channel, message);
            } else {
                info.eventTranslator.setEventValues(handler, channel, ChannelEvent.ChannelState.RECEIVED, null, message);
                try {
                    disruptor.publishEvent(info.eventTranslator);
                } catch (NullPointerException e) {
                    logger.error("The Disruptor queue has been closed, and the message is no longer received.");
                }
            }
        } else {
            handler.received(channel, message);
        }
    }

    @Override
    public void caught(Channel channel, Throwable throwable) throws TransportException {
        handler.caught(channel, throwable);
    }

    @Override
    public ChannelHandler getHandler() {
        return handler;
    }

    /**
     *
     */
    static class EventInfo {
        private final ChannelEventTranslator eventTranslator;

        EventInfo(ChannelEventTranslator eventTranslator) {
            this.eventTranslator = eventTranslator;
        }
    }
}