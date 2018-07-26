package cn.cloudwalk.smartframework.common.distributed;

import cn.cloudwalk.smartframework.common.IBaseComponent;

/**
 * Zookeeper服务注册
 * <p>
 * 负责在容器启动完成之后将所有加载的服务注册到响应路径
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IZookeeperRegister extends IBaseComponent {

    /**
     * 注册服务
     */
    void registerService();
}
