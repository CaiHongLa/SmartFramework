package cn.cloudwalk.smartframework.common.task;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LIYANHUI
 */
public abstract class BackgroundTask<V> implements IBackgroundTask<V> {

    protected Logger logger = LogManager.getLogger(this.getClass());

    public BackgroundTask() {
    }

    @Override
    public V call() throws Exception {
        try {
            return this.execute();
        } catch (Exception e) {
            this.logger.error("后台任务执行时捕获到异常", e);
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }
}
