package cn.cloudwalk.smartframework.transport.exchange.support.header;

import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * 心跳处理任务
 *
 * 备注：只有服务端使用了该线程处理客户端超时未发送数据的情况 客户端心跳由客户端自己处理
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
final class HeartBeatTask implements Runnable {

    private static final Logger logger = LogManager.getLogger(HeartBeatTask.class);

    private ChannelProvider channelProvider;

    private int heartbeat;

    private int heartbeatTimeout;

    HeartBeatTask(ChannelProvider provider, int heartbeat, int heartbeatTimeout) {
        this.channelProvider = provider;
        this.heartbeat = heartbeat;
        this.heartbeatTimeout = heartbeatTimeout;
    }

    @Override
    public void run() {
        try {
            long now = System.currentTimeMillis();
            for (Channel channel : channelProvider.getChannels()) {
                if (channel.isClosed()) {
                    continue;
                }
                try {
                    Long lastRead = (Long) channel.getAttribute(
                            HeaderExchangeHandler.KEY_READ_TIMESTAMP);
                    Long lastWrite = (Long) channel.getAttribute(
                            HeaderExchangeHandler.KEY_WRITE_TIMESTAMP);
                    if ((lastRead != null && now - lastRead > heartbeat)
                            || (lastWrite != null && now - lastWrite > heartbeat)) {
                        //发送心跳数据
                    }
                    if (lastRead != null && now - lastRead > heartbeatTimeout) {
                        logger.warn("关闭连接：" + channel
                                + ", 因为长时间没有收到数据，超时时间： " + heartbeatTimeout + "ms");
                        if (channel instanceof Client) {
                            try {
                                ((Client) channel).reconnect();
                            } catch (Exception e) {
                            }
                        } else {
                            channel.close();
                        }
                    }
                } catch (Throwable t) {
                    logger.warn("心跳线程异常，" + channel.getRemoteAddress(), t);
                }
            }
        } catch (Throwable t) {
            logger.warn("心跳线程出现不可控制的异常: " + t.getMessage(), t);
        }
    }

    interface ChannelProvider {
        Collection<Channel> getChannels();
    }

}

