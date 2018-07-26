package cn.cloudwalk.smartframework.common.distributed;

import cn.cloudwalk.smartframework.common.IBaseComponent;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁服务
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IDistributedLockService extends IBaseComponent {

    /**
     * 声明或获取锁
     *
     * @param lockName 锁名称
     * @param path     锁的节点路径
     */
    void definedOrUse(String lockName, String path);

    /**
     * 尝试获取锁（默认20S等待）
     *
     * @param lockName 锁名称
     * @return 成功true 失败false
     */
    boolean acquire(String lockName);

    /**
     * 尝试获取锁
     *
     * @param lockName   锁名称
     * @param timeToWait 等待时间
     * @param timeUnit   时间单位
     * @return 成功true 失败false
     */
    boolean acquire(String lockName, long timeToWait, TimeUnit timeUnit);

    /**
     * 释放锁
     *
     * @param lockName 锁名称
     */
    void release(String lockName);

    /**
     * 删除锁
     *
     * @param lockName 锁名称
     */
    void clear(String lockName);

    IZookeeperService getZookeeperService();
}
