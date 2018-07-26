package cn.cloudwalk.smartframework.rpc.zookeeper;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * 分布式锁
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class MutexLockInfo {

    private String name;
    private String path;
    private InterProcessMutex lock;

    public MutexLockInfo(String name, String path, InterProcessMutex lock) {
        this.name = name;
        this.path = path;
        this.lock = lock;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public InterProcessMutex getLock() {
        return this.lock;
    }

    public void setLock(InterProcessMutex lock) {
        this.lock = lock;
    }

    @Override
    public String toString() {
        return "MutexLock [name=" + this.name + ", path=" + this.path + ", lock=" + this.lock + "]";
    }
}
