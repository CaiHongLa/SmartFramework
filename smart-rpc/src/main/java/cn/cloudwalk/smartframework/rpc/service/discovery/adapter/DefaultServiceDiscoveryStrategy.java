package cn.cloudwalk.smartframework.rpc.service.discovery.adapter;

import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;
import cn.cloudwalk.smartframework.rpc.service.discovery.AbstractServiceDiscoveryStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 默认的服务发现策略，随机选择
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class DefaultServiceDiscoveryStrategy extends AbstractServiceDiscoveryStrategy {
    private static final Logger logger = LogManager.getLogger(DefaultServiceDiscoveryStrategy.class);

    private Random random = new Random();

    @Override
    public DistributedServiceProvider selectBestService(String path, List<DistributedServiceProvider> distributedServiceProviders) {
        Iterator<DistributedServiceProvider> distributedServiceProviderIterator = distributedServiceProviders.iterator();
        DistributedServiceProvider provider;
        do {
            if (!distributedServiceProviderIterator.hasNext()) {
                int index = this.random.nextInt(distributedServiceProviders.size());
                logger.info("当前主机不存在目标服务，因此准备执行远程服务：" + path + ", 可选择节点有 " + distributedServiceProviders.size() + " 个, 已随机选择第 " + (index + 1) + " 个");
                return distributedServiceProviders.get(index);
            }

            provider = distributedServiceProviderIterator.next();
        } while (!provider.getIp().equals(this.getZookeeperService().getAvailableLocalIp()));

        logger.info("发现当前主机存在目标服务，已优先使用本主机服务");
        return provider;
    }
}
