package cn.cloudwalk.smartframework.rpc.service.discovery.adapter;

import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;
import cn.cloudwalk.smartframework.rpc.service.discovery.AbstractServiceDiscoveryStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

/**
 * 随机服务发现策略
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/4/25 14:00
 * @since 2.0.0
 */
public class RandomServiceDiscoveryStrategy extends AbstractServiceDiscoveryStrategy {
    private static final Logger logger = LogManager.getLogger(RandomServiceDiscoveryStrategy.class);

    private Random random = new Random();

    @Override
    public DistributedServiceProvider selectBestService(String path, List<DistributedServiceProvider> distributedServiceProviders) {
        int index = this.random.nextInt(distributedServiceProviders.size());
        logger.info("There is no target service for the current host, so it is ready to perform remote services：" + path + ", optional nodes: " + distributedServiceProviders.size() + " , random select " + (index + 1));
        return distributedServiceProviders.get(index);
    }
}
