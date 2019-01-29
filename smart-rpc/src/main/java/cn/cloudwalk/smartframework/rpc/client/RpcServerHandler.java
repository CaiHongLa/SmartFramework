package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.transportcomponents.Channel;
import cn.cloudwalk.smartframework.transportcomponents.ChannelHandler;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportContext;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RpcServerHandler
 *
 * @author LIYANHUI
 * @since 2.0.10
 */
@io.netty.channel.ChannelHandler.Sharable
public class RpcServerHandler extends ChannelDuplexHandler {

    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    private final ChannelHandler handler;

    private TransportContext transportContext;

    public RpcServerHandler(TransportContext transportContext, ChannelHandler handler) {
        this.handler = handler;
        this.transportContext = transportContext;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        RpcChannel channel = RpcChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            if (channel != null) {
                channels.put(ctx.channel().remoteAddress().toString(), channel);
            }
            handler.connected(channel);
        } finally {
            RpcChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RpcChannel channel = RpcChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            channels.remove(ctx.channel().remoteAddress().toString());
            handler.disconnected(channel);
        } finally {
            RpcChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcChannel channel = RpcChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.received(channel, msg);
        } finally {
            RpcChannel.removeChannelIfDisconnected(ctx.channel());
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        RpcChannel channel = RpcChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.send(channel, msg);
        } finally {
            RpcChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        RpcChannel channel = RpcChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.caught(channel, cause);
        } finally {
            RpcChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }
}
