package cn.cloudwalk.smartframework.rpc.zookeeper;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IDistributedMutexLockService;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁获取服务
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("distributedMutexLockService")
public class DistributedMutexLockService extends BaseComponent implements IDistributedMutexLockService {
    private static final Logger logger = LogManager.getLogger(DistributedMutexLockService.class);
    private static ConcurrentMap<String, MutexLockInfo> lockList;
    private static ThreadLocal<Set<String>> ownerList;

    @Autowired
    @Qualifier("zookeeperService")
    private IZookeeperService zookeeperService;

    public DistributedMutexLockService() {
        lockList = new ConcurrentHashMap<>();
        ownerList = new ThreadLocal<>();
    }

    @Override
    public void definedOrUse(String lockName, String path) {
        if (lockList.containsKey(lockName)) {
            logger.info("已使用锁 " + lockName + "，在 " + path + " 上");
        } else {
            lockList.put(lockName, new MutexLockInfo(lockName, path, new InterProcessMutex((CuratorFramework) this.zookeeperService.getClient(), path)));
            logger.info("已声明锁 " + lockName + "，在 " + path + " 上");
        }
    }

    @Override
    public boolean acquire(String lockName) {
        long maxWaitToTime = 20L;
        return this.acquire(lockName, maxWaitToTime, TimeUnit.SECONDS);
    }

    @Override
    public boolean acquire(String lockName, long timeToWait, TimeUnit timeUnit) {
        MutexLockInfo lock = lockList.get(lockName);

        try {
            logger.info("正在尝试获取锁 " + lockName + "，最长等待时间 " + timeToWait + " " + timeUnit);
            boolean rs = lock.getLock().acquire(timeToWait, timeUnit);
            if (rs) {
                logger.info("锁 " + lockName + " 获取成功");
                if (ownerList.get() == null) {
                    Set<String> set = new HashSet<>();
                    ownerList.set(set);
                }

                ownerList.get().add(lockName);
            } else {
                logger.info("锁 " + lockName + " 获取失败");
            }

            return rs;
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    @Override
    public void release(String lockName) {
        logger.info("准备释放锁 " + lockName);
        Set<String> set = ownerList.get();
        if (set != null && set.contains(lockName)) {
            MutexLockInfo lock = lockList.get(lockName);
            try {
                lock.getLock().release();
                ownerList.get().remove(lockName);
                logger.info("锁 " + lockName + " 释放成功");
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        } else {
            logger.info("该线程不持有锁 " + lockName + ", 无需释放");
        }

    }

    @Override
    public void clear(String lockName) {
        logger.info("准备清理锁 " + lockName);
        MutexLockInfo info = lockList.get(lockName);
        if (info != null) {
            this.zookeeperService.deletePath(info.getPath());
            logger.info("已成功清理锁 " + lockName);
        } else {
            logger.info("没有找到锁名称 " + lockName + "，清理操作无效");
        }

    }

    @Override
    public IZookeeperService getZookeeperService() {
        return this.zookeeperService;
    }

    @PreDestroy
    public void destroy(){
        ownerList.remove();
    }
}