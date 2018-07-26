package cn.cloudwalk.smartframework.transport.exchange.support.header;


import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.Server;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeChannel;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeServer;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * HeaderExchangeServer
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class HeaderExchangeServer implements ExchangeServer {

    private static final Logger logger = LogManager.getLogger(HeaderExchangeServer.class);

    private final ScheduledExecutorService scheduled = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("HeaderExchangeServer-Schedule").setDaemon(true).build());

    private final Server server;
    private ScheduledFuture<?> heartbeatTimer;
    private int heartbeat;
    private int heartbeatTimeout;
    private AtomicBoolean closed = new AtomicBoolean(false);

    public HeaderExchangeServer(Server server) {
        if (server == null) {
            throw new IllegalArgumentException("server == null");
        }
        this.server = server;
        this.heartbeat = server.getTransportContext().getParameter(ProtocolConstants.EXCHANGE_HEART_BEAT_TIME, 60000);
        this.heartbeatTimeout = server.getTransportContext().getParameter(ProtocolConstants.EXCHANGE_HEART_BEAT_TIMEOUT, 300000);
        if (heartbeatTimeout < heartbeat * 2) {
            throw new IllegalStateException(ProtocolConstants.EXCHANGE_HEART_BEAT_TIMEOUT + "应该不小于" + ProtocolConstants.EXCHANGE_HEART_BEAT_TIME + "的两倍！");
        }
        startHeartbeatTimer();
    }

    public Server getServer() {
        return server;
    }

    @Override
    public boolean isClosed() {
        return server.isClosed();
    }

    @Override
    public void close() {
        doClose();
        server.close();
    }

    @Override
    public void startClose() {
        server.startClose();
    }

    private void doClose() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        stopHeartbeatTimer();
        try {
            scheduled.shutdown();
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    @Override
    public Collection<ExchangeChannel> getExchangeChannels() {
        Collection<ExchangeChannel> exchangeChannels = new ArrayList<>();
        Collection<Channel> channels = server.getChannels();
        if (channels != null && channels.size() > 0) {
            for (Channel channel : channels) {
                exchangeChannels.add(HeaderExchangeChannel.getOrAddChannel(channel));
            }
        }
        return exchangeChannels;
    }

    @Override
    public ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress) {
        Channel channel = server.getChannel(remoteAddress);
        return HeaderExchangeChannel.getOrAddChannel(channel);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Collection<Channel> getChannels() {
        return (Collection) getExchangeChannels();
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        return getExchangeChannel(remoteAddress);
    }

    @Override
    public boolean isBind() {
        return server.isBind();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return server.getLocalAddress();
    }

    @Override
    public TransportContext getTransportContext() {
        return server.getTransportContext();
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return server.getChannelHandler();
    }

    @Override
    public void reset(TransportContext transportContext) {
        server.reset(transportContext);
        try {
            startHeartbeatTimer();
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    @Override
    public void send(Object message) throws TransportException {
        if (closed.get()) {
            throw new TransportException(this.getLocalAddress(), null, "发送消息：" + message + "失败， " + getLocalAddress() + " 已经关闭!");
        }
        server.send(message);
    }

    @Override
    public void send(Object message, boolean sent) throws TransportException {
        if (closed.get()) {
            throw new TransportException(this.getLocalAddress(), null, "发送消息：" + message + "失败， " + getLocalAddress() + " 已经关闭!");
        }
        server.send(message, sent);
    }

    private void startHeartbeatTimer() {
        stopHeartbeatTimer();
        if (heartbeat > 0) {
            heartbeatTimer = scheduled.scheduleWithFixedDelay(
                    new HeartBeatTask(() -> Collections.unmodifiableCollection(
                            HeaderExchangeServer.this.getChannels()), heartbeat, heartbeatTimeout),
                    heartbeat, heartbeat, TimeUnit.MILLISECONDS);
        }
    }

    private void stopHeartbeatTimer() {
        try {
            ScheduledFuture<?> timer = heartbeatTimer;
            if (timer != null && !timer.isCancelled()) {
                timer.cancel(true);
            }
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        } finally {
            heartbeatTimer = null;
        }
    }

}