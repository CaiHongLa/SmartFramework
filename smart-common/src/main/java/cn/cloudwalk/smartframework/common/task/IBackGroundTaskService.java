package cn.cloudwalk.smartframework.common.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author LIYANHUI
 */
public interface IBackGroundTaskService {

    <V> Future<V> submit(BackgroundTask<V> task);

    ExecutorService getExecutorService();
}
