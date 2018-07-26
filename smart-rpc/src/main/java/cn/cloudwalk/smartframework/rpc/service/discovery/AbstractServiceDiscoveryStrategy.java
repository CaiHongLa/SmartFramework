package cn.cloudwalk.smartframework.rpc.service.discovery;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IServiceDiscoveryStrategy;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;

/**
 * 服务发现策略
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public abstract class AbstractServiceDiscoveryStrategy extends BaseComponent implements IServiceDiscoveryStrategy {

    private IZookeeperService zookeeperService;

    @Override
    public IZookeeperService getZookeeperService() {
        return zookeeperService;
    }

    @Override
    public void setZookeeperService(IZookeeperService zookeeperService) {
        this.zookeeperService = zookeeperService;
    }
}
