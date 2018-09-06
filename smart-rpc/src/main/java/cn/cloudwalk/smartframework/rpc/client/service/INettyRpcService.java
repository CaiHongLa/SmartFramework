package cn.cloudwalk.smartframework.rpc.client.service;

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

    Server getRpcServer();

    Integer getRpcPort();
}