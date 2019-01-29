package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.transportcomponents.ChannelHandler;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportContext;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * RpcClientHandler
 *
 * @author LIYANHUI
 * @since 2.0.10
 */
@io.netty.channel.ChannelHandler.Sharable
public class RpcClientHandler extends ChannelDuplexHandler {

    private final TransportContext transportContext;

    private final ChannelHandler handler;

    public RpcClientHandler(TransportContext transportContext, ChannelHandler handler) {
        this.transportContext = transportContext;
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        RpcChannel channel = RpcChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
            handler.connected(channel);
        } finally {
            RpcChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RpcChannel channel = RpcChannel.getOrAddChannel(transportContext, ctx.channel(), handler);
        try {
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
