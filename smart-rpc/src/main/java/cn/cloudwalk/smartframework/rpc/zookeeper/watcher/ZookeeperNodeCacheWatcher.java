package cn.cloudwalk.smartframework.rpc.zookeeper.watcher;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperNodeCacheWatcher;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.Closeable;

/**
 * 节点监听器
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("zookeeperNodeCacheWatcher")
public class ZookeeperNodeCacheWatcher extends BaseComponent implements IZookeeperNodeCacheWatcher {

    private TreeCache cache;
    private static final Logger logger = LogManager.getLogger(ZookeeperNodeCacheWatcher.class);

    @Override
    public void watch(Closeable client, String path) {
        logger.info("start zookeeper watcher（path=" + path + "）");
        this.cache = new TreeCache((CuratorFramework) client, path);
        try {
            this.cache.start();
            logger.info("zookeeper watcher started");
        } catch (Exception var4) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(var4));
        }
    }

    @Override
    public TreeCache getCache() {
        return this.cache;
    }

    /**
     * @since 2.0.10
     */
    @PreDestroy
    public void destroy(){
        if(cache != null){
            cache.close();
        }
    }

}
