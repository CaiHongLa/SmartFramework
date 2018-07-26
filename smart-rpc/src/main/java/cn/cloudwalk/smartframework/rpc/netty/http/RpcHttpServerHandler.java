package cn.cloudwalk.smartframework.rpc.netty.http;

import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接收处理Rpc Http请求
 *
 * @author LIYANHUI
 * @since 2.0.0
 */
@io.netty.channel.ChannelHandler.Sharable
public class RpcHttpServerHandler extends ChannelDuplexHandler {

    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    private final ChannelHandler handler;

    private TransportContext transportContext;

    public RpcHttpServerHandler(TransportContext transportContext, ChannelHandler handler) {
        this.handler = handler;
        this.transportContext = transportContext;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        RpcHttpChannel channel = RpcHttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            if (channel != null) {
                channels.put(ctx.channel().remoteAddress().toString(), channel);
            }
            handler.connected(channel);
        } finally {
            RpcHttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RpcHttpChannel channel = RpcHttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            channels.remove(ctx.channel().remoteAddress().toString());
            handler.disconnected(channel);
        } finally {
            RpcHttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcHttpChannel channel = RpcHttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.received(channel, msg);
        } finally {
            RpcHttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        RpcHttpChannel channel = RpcHttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.send(channel, msg);
        } finally {
            RpcHttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        RpcHttpChannel channel = RpcHttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.caught(channel, cause);
        } finally {
            RpcHttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }
}
