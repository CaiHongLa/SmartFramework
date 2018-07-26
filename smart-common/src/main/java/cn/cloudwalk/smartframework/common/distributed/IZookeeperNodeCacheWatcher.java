package cn.cloudwalk.smartframework.common.distributed;

import cn.cloudwalk.smartframework.common.IBaseComponent;

import java.io.Closeable;

/**
 * Zookeeper节点监听器
 * <p>
 * 负责监听节点的变化，并实时更新节点缓存
 *
 * @author LIYANHUI
 * @see org.apache.curator.framework.recipes.cache.TreeCache;
 * @see org.apache.curator.framework.recipes.cache.TreeCacheEvent;
 * @since 1.0.0
 */
public interface IZookeeperNodeCacheWatcher extends IBaseComponent {

    /**
     * 初始化监听节点
     *
     * @param client zookeeper客户端实例
     * @param path   监听的节点
     */
    void watch(Closeable client, String path);

    /**
     * 获取几点缓存数据
     *
     * @return TreeCache
     */
    Closeable getCache();

}
