package cn.cloudwalk.smartframework.transport;


/**
 * Handler装饰器
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface ChannelHandlerDelegate extends ChannelHandler{

    /**
     * 获取装饰的Handler
     *
     * @return ChannelHandler
     */
    ChannelHandler getHandler();
}
