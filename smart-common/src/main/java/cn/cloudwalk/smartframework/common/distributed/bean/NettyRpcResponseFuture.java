package cn.cloudwalk.smartframework.common.distributed.bean;

import cn.cloudwalk.smartframework.common.distributed.AsyncCallBack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Rpc异步回调结果。
 *
 * @author 李延辉
 * @see java.util.concurrent.Future
 * @see NettyRpcResponse
 * @since 1.0.0
 */
public class NettyRpcResponseFuture implements Future<NettyRpcResponse> {

    private Logger logger = LogManager.getLogger(NettyRpcResponseFuture.class);

    /**
     * 同步
     */
    private Sync sync;

    /**
     * rpc结果
     */
    private NettyRpcResponse response;

    /**
     * 请求开始时间
     */
    private long startTime;

    /**
     * 读写锁 线程安全
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 请求编号
     */
    private String requestId;

    /**
     * 接口地址
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 回调集合
     */
    private List<AsyncCallBack> pendingCallbacks = new ArrayList<>();

    public NettyRpcResponseFuture(String requestId, String className, String methodName) {
        this.sync = new Sync();
        this.startTime = System.currentTimeMillis();
        this.requestId = requestId;
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public NettyRpcResponse get() {
        sync.acquire(-1);
        if (this.response != null) {
            return this.response;
        } else {
            return null;
        }
    }

    @Override
    public NettyRpcResponse get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.response != null) {
                return this.response;
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("RPC timeout " + requestId
                    + ". Class: " + className
                    + ". Method: " + methodName);
        }
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    /**
     * 调用完成
     *
     * @param response 返回结果
     */
    public void done(NettyRpcResponse response) {
        this.response = response;
        sync.release(1);
        invokeCallbacks();
        long responseTime = System.currentTimeMillis() - startTime;
        long responseTimeThreshold = 5000;
        if (responseTime > responseTimeThreshold) {
            logger.warn("RPC takes long time " + response.getRequestId() + ". take： " + responseTime + "ms");
        }
    }

    /**
     * 逐个回调
     */
    private void invokeCallbacks() {
        lock.lock();
        try {
            for (final AsyncCallBack callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加一个回调时间
     *
     * @param callback AsyncCallBack
     * @return NettyRpcResponseFuture
     */
    public NettyRpcResponseFuture addCallback(AsyncCallBack callback) {
        lock.lock();
        try {
            if (isDone()) {
                runCallback(callback);
            } else {
                this.pendingCallbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    private void runCallback(final AsyncCallBack callback) {
        if (!response.isError()) {
            callback.onSuccess(response.getResult());
        } else {
            callback.onError(response.getError());
        }
    }

    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        /**
         * future status
         */
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int acquires) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int releases) {
            if (getState() == pending) {
                return compareAndSetState(pending, done);
            }
            return false;
        }

        boolean isDone() {
            return getState() == done;
        }
    }
}
