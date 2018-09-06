package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.common.util.NettySslConfigUtil;
import cn.cloudwalk.smartframework.transport.AbstractServer;
import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.Server;
import cn.cloudwalk.smartframework.transport.support.ChannelHandlers;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * RpcServer
 *
 * @author LIYANHUI
 * @since 2.0.10
 */
public class RpcServer extends AbstractServer implements Server {

    private static final Logger logger = LogManager.getLogger(RpcServer.class);
    private Map<String, Channel> channels;
    private ServerBootstrap bootstrap;
    private io.netty.channel.Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors() + 1;

    public RpcServer(TransportContext transportContext, ChannelHandler handler) {
        super(transportContext, ChannelHandlers.wrapServer(transportContext, handler));
    }

    @Override
    protected void doOpen() {
        bootstrap = new ServerBootstrap();
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(getTransportContext().getParameter(ProtocolConstants.RPC_SERVER_BOSS_THREAD_SIZE, AVAILABLE_PROCESSORS),
                    new DefaultThreadFactory("RpcEpollServerBoss", true));
            workerGroup = new EpollEventLoopGroup(getTransportContext().getParameter(ProtocolConstants.RPC_SERVER_WORKER_THREAD_SIZE, AVAILABLE_PROCESSORS),
                    new DefaultThreadFactory("RpcEpollServerWorker", true));
            bootstrap.channel(EpollServerSocketChannel.class);
        } else {
            bossGroup = new NioEventLoopGroup(getTransportContext().getParameter(ProtocolConstants.RPC_SERVER_BOSS_THREAD_SIZE, AVAILABLE_PROCESSORS),
                    new DefaultThreadFactory("RpcNioServerBoss", true));
            workerGroup = new NioEventLoopGroup(getTransportContext().getParameter(ProtocolConstants.RPC_SERVER_WORKER_THREAD_SIZE, AVAILABLE_PROCESSORS),
                    new DefaultThreadFactory("RpcNioServerWorker", true));
            bootstrap.channel(NioServerSocketChannel.class);
        }
        final RpcServerHandler rpcServerHandler = new RpcServerHandler(getTransportContext(), this);
        channels = rpcServerHandler.getChannels();
        bootstrap.group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<io.netty.channel.Channel>() {
                    @Override
                    protected void initChannel(io.netty.channel.Channel ch) {
                        RpcCodecAdapter adapter = new RpcCodecAdapter(getTransportContext(), getCodec(), RpcServer.this);
                        ChannelPipeline pipeline = ch.pipeline();
                        NettySslConfigUtil.addSslHandler(pipeline, "tcp");
                        pipeline.addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("handler", rpcServerHandler);
                    }
                });

        ChannelFuture channelFuture = bootstrap.bind(getBindAddress());
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();
    }

    @Override
    protected void doClose() {
        try {
            if (channel != null) {
                channel.close();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            Collection<Channel> channels = getChannels();
            if (channels != null && channels.size() > 0) {
                for (Channel channel : channels) {
                    try {
                        channel.close();
                    } catch (Throwable e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            if (bootstrap != null) {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            if (channels != null) {
                channels.clear();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public boolean isBind() {
        return channel.isActive();
    }

    @Override
    public Collection<Channel> getChannels() {
        Collection<Channel> chs = new HashSet<>();
        for (Channel channel : this.channels.values()) {
            if (channel.isConnected()) {
                chs.add(channel);
            } else {
                channels.remove(channel.getRemoteAddress().toString());
            }
        }
        return chs;
    }

    @Override
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        return channels.get(inetSocketAddress.toString());
    }
}