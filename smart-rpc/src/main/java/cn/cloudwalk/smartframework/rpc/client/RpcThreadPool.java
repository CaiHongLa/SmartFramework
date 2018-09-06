package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.transport.ThreadPool;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * RpcThreadPool
 *
 * @see Executors#newFixedThreadPool
 * @since 2.0.10
 */
public class RpcThreadPool implements ThreadPool {

    @Override
    public Executor newExecutor(TransportContext transportContext) {
        return Executors.newFixedThreadPool(20);
    }
}
