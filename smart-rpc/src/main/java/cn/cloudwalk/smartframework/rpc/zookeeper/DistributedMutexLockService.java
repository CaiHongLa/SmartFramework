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
            logger.info("used lock " + lockName + " from " + path);
        } else {
            lockList.put(lockName, new MutexLockInfo(lockName, path, new InterProcessMutex((CuratorFramework) this.zookeeperService.getClient(), path)));
            logger.info("define lock " + lockName + " in " + path);
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
            logger.info("trying to get the lock " + lockName + "，max wait time " + timeToWait + " " + timeUnit);
            boolean rs = lock.getLock().acquire(timeToWait, timeUnit);
            if (rs) {
                logger.info("lock " + lockName + " get successfully");
                if (ownerList.get() == null) {
                    Set<String> set = new HashSet<>();
                    ownerList.set(set);
                }

                ownerList.get().add(lockName);
            } else {
                logger.info("lock " + lockName + " get failed");
            }

            return rs;
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    @Override
    public void release(String lockName) {
        logger.info("trying to release lock " + lockName);
        Set<String> set = ownerList.get();
        if (set != null && set.contains(lockName)) {
            MutexLockInfo lock = lockList.get(lockName);
            try {
                lock.getLock().release();
                ownerList.get().remove(lockName);
                logger.info("lock " + lockName + " release successfully");
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        } else {
            logger.info("the thread does not own the lock " + lockName + ", no need to release");
        }

    }

    @Override
    public void clear(String lockName) {
        logger.info("trying to clear lock " + lockName);
        MutexLockInfo info = lockList.get(lockName);
        if (info != null) {
            this.zookeeperService.deletePath(info.getPath());
            logger.info("cleared lock " + lockName);
        } else {
            logger.info("not found lock " + lockName + "，clear operation is invalid");
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