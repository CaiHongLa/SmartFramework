package cn.cloudwalk.smartframework.rpc.netty;

import cn.cloudwalk.smartframework.common.IBaseComponent;
import cn.cloudwalk.smartframework.transport.Server;

/**
 * Rpc服务启动
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface INettyRpcService extends IBaseComponent {

    void start();

    Server getNettyRpcServer();

    Integer getHttpRpcPort();
}