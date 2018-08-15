package cn.cloudwalk.smartframework.task;

import cn.cloudwalk.smartframework.common.IConfigurationService;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.task.BackgroundTask;
import cn.cloudwalk.smartframework.common.task.IBackGroundTaskService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * BackGroundTaskService
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("backGroundTaskService")
public class BackGroundTaskService implements IBackGroundTaskService {

    private ExecutorService executorService;

    private static final Logger logger = LogManager.getLogger(BackGroundTaskService.class);

    @Autowired(required = false)
    private IConfigurationService configurationService;

    @PostConstruct
    private void init() {
        if(null == configurationService){
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("IConfigurationService is null，please import Config module in your application context ！"));
        }
        Properties config = configurationService.getApplicationCfg();
        int value = 1000;
        if (config != null) {
            value = Integer.parseInt(config.getProperty("thread.task.pool", "1000"));
        }
        logger.info("thread.task.pool value  " + value + "（If not set, the default value is 1000.）");
        executorService = new ThreadPoolExecutor(0, value,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), new ThreadFactoryBuilder()
                .setNameFormat("BackGroundTaskService-pool").build(), new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public <V> Future<V> submit(BackgroundTask<V> task) {
        return executorService.submit(task);
    }

    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }

    @PreDestroy
    private void destroy() {
        if (this.executorService != null) {
            logger.info("Start closing the background task thread pool.");
            this.executorService.shutdownNow();
            logger.info("Background task thread pool closed successfully");
        }
    }
}
