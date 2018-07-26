package cn.cloudwalk.smartframework.transport.exchange.support.header;

import cn.cloudwalk.smartframework.common.task.NamedThreadFactory;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.Client;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeChannel;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeClient;
import cn.cloudwalk.smartframework.transport.exchange.ExchangeHandler;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * HeaderExchangeClient
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class HeaderExchangeClient implements ExchangeClient {

    private static final Logger logger = LogManager.getLogger(HeaderExchangeClient.class);

    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("HeaderExchangeClientHeartBeat", true));
    private final Client client;
    private final ExchangeChannel channel;
    private ScheduledFuture<?> heartbeatTimer;
    private int heartbeat;
    private int heartbeatTimeout;

    public HeaderExchangeClient(Client client, boolean needHeartbeat) {
        if (client == null) {
            throw new IllegalArgumentException("client不能为空！");
        }
        this.client = client;
        this.channel = new HeaderExchangeChannel(client);
        this.heartbeat = client.getTransportContext().getParameter(ProtocolConstants.EXCHANGE_HEART_BEAT_TIME, 60000);
        this.heartbeatTimeout = client.getTransportContext().getParameter(ProtocolConstants.EXCHANGE_HEART_BEAT_TIMEOUT, 300000);
        if (heartbeatTimeout < heartbeat * 2) {
            throw new IllegalStateException(ProtocolConstants.EXCHANGE_HEART_BEAT_TIMEOUT + "应该不小于" + ProtocolConstants.EXCHANGE_HEART_BEAT_TIME + "的两倍！");
        }
        if (needHeartbeat) {
            startHeartbeatTimer();
        }
    }

    @Override
    public TransportContext getTransportContext() {
        return channel.getTransportContext();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return channel.getChannelHandler();
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return channel.getLocalAddress();
    }

    @Override
    public ExchangeHandler getExchangeHandler() {
        return channel.getExchangeHandler();
    }

    @Override
    public void send(Object message) throws TransportException {
        channel.send(message);
    }

    @Override
    public void send(Object message, boolean sent) throws TransportException {
        channel.send(message, sent);
    }

    @Override
    public boolean isClosed() {
        return channel.isClosed();
    }

    @Override
    public void close() {
        startClose();
        doClose();
        channel.close();
    }

    @Override
    public void startClose() {
        channel.startClose();
    }

    @Override
    public void reset(TransportContext transportContext) {
        client.reset(transportContext);
    }

    @Override
    public void reconnect() throws TransportException {
        client.reconnect();
    }

    @Override
    public Object getAttribute(String key) {
        return channel.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        channel.setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        channel.removeAttribute(key);
    }

    @Override
    public boolean hasAttribute(String key) {
        return channel.hasAttribute(key);
    }

    private void startHeartbeatTimer() {
        stopHeartbeatTimer();
        if (heartbeat > 0) {
            heartbeatTimer = scheduled.scheduleWithFixedDelay(
                    new HeartBeatTask(() -> Collections.singletonList(HeaderExchangeClient.this), heartbeat, heartbeatTimeout),
                    heartbeat, heartbeat, TimeUnit.MILLISECONDS);
        }
    }

    private void stopHeartbeatTimer() {
        if (heartbeatTimer != null && !heartbeatTimer.isCancelled()) {
            try {
                heartbeatTimer.cancel(true);
                scheduled.purge();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        }
        heartbeatTimer = null;
    }

    private void doClose() {
        stopHeartbeatTimer();
    }

    @Override
    public String toString() {
        return "HeaderExchangeClient [channel=" + channel + "]";
    }
}