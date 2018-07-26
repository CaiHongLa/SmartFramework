package cn.cloudwalk.smartframework.transport;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * 服务接口
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface Server extends EndPoint {

    /**
     * 服务是否已经绑定
     *
     * @return 已经绑定：true 否则：false
     */
    boolean isBind();

    /**
     * 获取服务的所有连接
     *
     * @return Channel集合
     */
    Collection<Channel> getChannels();

    /**
     * 根据某个地址获取连接
     *
     * @param inetSocketAddress 远端地址
     * @return Channel
     */
    Channel getChannel(InetSocketAddress inetSocketAddress);
}
