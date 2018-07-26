package cn.cloudwalk.smartframework.transport;

import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * 编解码接口
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface Codec {

    /**
     * 解码
     *
     * @param channel 连接
     * @param byteBuf 输入buffer
     * @param out     输出解码对象
     * @throws Exception
     */
    void decode(Channel channel, ByteBuf byteBuf, List<Object> out) throws Exception;

    /**
     * 编码
     *
     * @param channel 连接
     * @param byteBuf 输出buffer
     * @param msg     输入对象
     * @throws Exception
     */
    void encode(Channel channel, ByteBuf byteBuf, Object msg) throws Exception;
}
