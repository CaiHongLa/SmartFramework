package cn.cloudwalk.smartframework.common.distributed;

/**
 * Zookeeper状态变更监听器
 * 主要监听重连和连接状态
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IZookeeperWatcher {

    /**
     * 状态变更时的处理
     *
     * @param connectionState {@link ConnectionState}
     */
    void process(ConnectionState connectionState);

    enum ConnectionState {
        CONNECTED,
        SUSPENDED,
        RECONNECTED,
        LOST,
        READ_ONLY
    }

}
