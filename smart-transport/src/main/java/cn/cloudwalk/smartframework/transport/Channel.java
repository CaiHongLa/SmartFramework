package cn.cloudwalk.smartframework.transport;

import java.net.InetSocketAddress;

/**
 * 连接
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface Channel extends EndPoint {

    /**
     * 对端地址
     *
     * @return InetSocketAddress
     */
    InetSocketAddress getRemoteAddress();

    /**
     * 是否建立 是否存活
     *
     * @return 存活：true 断开：false
     */
    boolean isConnected();

    /**
     * 是否包含属性
     *
     * @param key 属性Key
     * @return 包含：true 不包含：false
     */
    boolean hasAttribute(String key);

    /**
     * 获取某个属性值
     *
     * @param key 属性Key
     * @return 值
     */
    Object getAttribute(String key);

    /**
     * 设置某个属性
     *
     * @param key   属性Key
     * @param value 值
     */
    void setAttribute(String key, Object value);

    /**
     * 移除某个属性
     *
     * @param key 属性Key
     */
    void removeAttribute(String key);
}
