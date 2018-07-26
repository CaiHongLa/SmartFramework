package cn.cloudwalk.smartframework.task;

import cn.cloudwalk.smartframework.common.task.BackgroundTask;
import cn.cloudwalk.smartframework.common.task.IBackGroundTaskService;
import cn.cloudwalk.smartframework.common.util.PropertiesUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    @PostConstruct
    private void init() {
        Properties config = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir("application-cfg.properties");
        int value = 1000;
        if (config != null) {
            value = Integer.parseInt(config.getProperty("thread.task.pool", "1000"));
        }
        logger.info("thread.task.pool 的值为 " + value + "（如果没有设置，则默认值为 1000）");
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
            logger.info("开始关闭后台任务线程池");
            this.executorService.shutdownNow();
            logger.info("后台任务线程池关闭成功");
        }
    }
}
