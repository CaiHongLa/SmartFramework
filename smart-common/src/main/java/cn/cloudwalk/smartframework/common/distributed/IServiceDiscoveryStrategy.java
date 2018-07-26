package cn.cloudwalk.smartframework.common.distributed;

import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;

import java.util.List;

/**
 * 服务发现策略
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IServiceDiscoveryStrategy {

    /**
     * 获取服务
     *
     * @param path                        服务路径
     * @param distributedServiceProviders 服务可选列表
     * @return 服务
     */
    DistributedServiceProvider selectBestService(String path, List<DistributedServiceProvider> distributedServiceProviders);

    IZookeeperService getZookeeperService();

    void setZookeeperService(IZookeeperService zookeeperService);
}
