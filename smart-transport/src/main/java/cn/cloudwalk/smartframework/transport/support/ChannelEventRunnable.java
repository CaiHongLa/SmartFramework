package cn.cloudwalk.smartframework.transport.support;

import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ChannelEventRunnable
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class ChannelEventRunnable implements Runnable {
    private static final Logger logger = LogManager.getLogger(ChannelEventRunnable.class);

    private final ChannelHandler handler;
    private final Channel channel;
    private final ChannelState state;
    private final Throwable exception;
    private final Object message;

    public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state) {
        this(channel, handler, state, null);
    }

    public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state, Object message) {
        this(channel, handler, state, message, null);
    }

    public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state, Throwable t) {
        this(channel, handler, state, null, t);
    }

    public ChannelEventRunnable(Channel channel, ChannelHandler handler, ChannelState state, Object message, Throwable exception) {
        this.channel = channel;
        this.handler = handler;
        this.state = state;
        this.message = message;
        this.exception = exception;
    }

    @Override
    public void run() {
        switch (state) {
            case CONNECTED:
                try {
                    handler.connected(channel);
                } catch (Exception e) {
                    logger.warn("ChannelEventRunnable处理" + state + "异常, 连接：" + channel, e);
                }
                break;
            case DISCONNECTED:
                try {
                    handler.disconnected(channel);
                } catch (Exception e) {
                    logger.warn("ChannelEventRunnable处理" + state + "异常, 连接：" + channel, e);
                }
                break;
            case SENT:
                try {
                    handler.send(channel, message);
                } catch (Exception e) {
                    logger.warn("ChannelEventRunnable处理 " + state + "异常, 连接：" + channel
                            + ", 消息：" + message, e);
                }
                break;
            case RECEIVED:
                try {
                    handler.received(channel, message);
                } catch (Exception e) {
                    logger.warn("ChannelEventRunnable处理 " + state + "异常, 连接：" + channel
                            + ", 消息：" + message, e);
                }
                break;
            case CAUGHT:
                try {
                    handler.caught(channel, exception);
                } catch (Exception e) {
                    logger.warn("ChannelEventRunnable处理 " + state + "异常, 连接：" + channel
                            + ", 消息：" + message + ",异常：" + e.getMessage(), e);
                }
                break;
            default:
                logger.warn("未知状态: " + state + ", 消息：" + message);
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
