package cn.cloudwalk.smartframework.http;

import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.ChannelHandler;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接收处理Http请求
 *
 * @author LIYANHUI
 * @date 2018/1/31
 * @since 1.0.0
 */
@io.netty.channel.ChannelHandler.Sharable
public class HttpServerHandler extends ChannelDuplexHandler {

    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    private final ChannelHandler handler;

    private TransportContext transportContext;

    public HttpServerHandler(TransportContext transportContext, ChannelHandler handler) {
        this.handler = handler;
        this.transportContext = transportContext;
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
        HttpChannel channel = HttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            if (channel != null) {
                channels.put(ctx.channel().remoteAddress().toString(), channel);
            }
            handler.connected(channel);
        } finally {
            HttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        HttpChannel channel = HttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            channels.remove(ctx.channel().remoteAddress().toString());
            handler.disconnected(channel);
        } finally {
            HttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpChannel channel = HttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.received(channel, msg);
        } finally {
            HttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        HttpChannel channel = HttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.send(channel, msg);
        } finally {
            HttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        HttpChannel channel = HttpChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.caught(channel, cause);
        } finally {
            HttpChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }
}
