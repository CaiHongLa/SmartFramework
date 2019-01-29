package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.transportcomponents.AbstractClient;
import cn.cloudwalk.smartframework.transportcomponents.ChannelHandler;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Rpc客户端
 *
 * @author LIYANHUI
 * @since 2.0.10
 */
public class RpcClient extends AbstractClient {

    private static final Logger logger = LogManager.getLogger(RpcClient.class);

    private Bootstrap bootstrap;
    private volatile Channel channel;
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors() + 1;

    public RpcClient(final TransportContext transportContext, final ChannelHandler handler) {
        super(transportContext, AbstractClient.wrapChannelHandler(transportContext, handler));
    }

    @Override
    protected void doOpen() {
        final RpcClientHandler rpcClientHandler = new RpcClientHandler(getTransportContext(), this);
        bootstrap = new Bootstrap();
        if (Epoll.isAvailable()) {
            EventLoopGroup eventLoopGroup = new EpollEventLoopGroup(AVAILABLE_PROCESSORS, new DefaultThreadFactory("RpcEpollClientWorker", true));
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(EpollSocketChannel.class);
        } else {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup(AVAILABLE_PROCESSORS, new DefaultThreadFactory("RpcNioClientWorker", true));
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
        }
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        if (getTimeout() < 3000) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        } else {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getTimeout());
        }

        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                RpcCodecAdapter adapter = new RpcCodecAdapter(getTransportContext(), getCodec(), RpcClient.this);
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", adapter.getDecoder())
                        .addLast("encoder", adapter.getEncoder())
                        .addLast("handler", rpcClientHandler);
            }
        });
    }

    @Override
    protected void doConnect() throws Throwable {
        ChannelFuture future = bootstrap.connect(getConnectAddress());
        try {
            boolean ret = future.awaitUninterruptibly(getConnectTimeout(), TimeUnit.MILLISECONDS);

            if (ret && future.isSuccess()) {
                Channel newChannel = future.channel();
                try {
                    Channel oldChannel = RpcClient.this.channel;
                    if (oldChannel != null) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("close old channel：" + oldChannel + "，create new channel：" + newChannel);
                            }
                            oldChannel.close();
                        } finally {
                            RpcChannel.removeChannelIfDisconnected(oldChannel);
                        }
                    }
                } finally {
                    if (RpcClient.this.isClosed()) {
                        try {
                            logger.warn("close new channel： " + newChannel + ", because client is closed！");
                            newChannel.close();
                        } finally {
                            RpcClient.this.channel = null;
                            RpcChannel.removeChannelIfDisconnected(newChannel);
                        }
                    } else {
                        RpcClient.this.channel = newChannel;
                    }
                }
            } else if (future.cause() != null) {
                throw future.cause();
            } else {
            }
        } finally {
        }
    }

    @Override
    protected void doDisConnect() {
        try {
            RpcChannel.removeChannelIfDisconnected(channel);
        } catch (Throwable t) {
            logger.warn(t.getMessage());
        }
    }

    @Override
    protected void doClose() {
        if (bootstrap != null) {
            EventLoopGroup eventLoopGroup = bootstrap.config().group();
            eventLoopGroup.shutdownGracefully(1, 5, TimeUnit.SECONDS);
            try {
                eventLoopGroup.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    @Override
    protected cn.cloudwalk.smartframework.transportcomponents.Channel getChannel() {
        Channel c = channel;
        if (c == null || !c.isActive()) {
            return null;
        }
        return RpcChannel.getOrAddChannel(getTransportContext(), c, this);
    }
}