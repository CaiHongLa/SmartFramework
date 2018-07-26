package cn.cloudwalk.smartframework.transport;


import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;

import java.util.concurrent.Executor;

/**
 * 线程池
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface ThreadPool {

    /**
     * 获取线程池实例
     *
     * @param transportContext 协议上下文
     * @return Executor
     */
    Executor newExecutor(TransportContext transportContext);

    /**
     * 获取创建的线程池
     *
     * @return newExecutor产生的线程池
     */
    Executor getExecutor();
}
