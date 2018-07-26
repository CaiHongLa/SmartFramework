package cn.cloudwalk.smartframework.common.task;

import java.util.concurrent.Callable;

/**
 * @author LIYANHUI
 */
public interface IBackgroundTask<V> extends Callable<V> {

    V execute() throws Exception;
}
