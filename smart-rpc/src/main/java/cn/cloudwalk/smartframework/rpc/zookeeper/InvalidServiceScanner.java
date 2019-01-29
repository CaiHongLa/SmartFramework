package cn.cloudwalk.smartframework.rpc.zookeeper;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IDistributedMutexLockService;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;

/**
 * 无效服务扫描服务
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component
public class InvalidServiceScanner extends BaseComponent {
    private static final Logger logger = LogManager.getLogger(InvalidServiceScanner.class);

    @Autowired
    @Qualifier("distributedMutexLockService")
    private IDistributedMutexLockService distributedMutexLockService;

    private String LOCK_NAME;
    private String LOCK_PATH;
    private int MAX_DELAY_SECONDS;
    private Random random;
    private boolean configCenter = false;

    public InvalidServiceScanner() {
    }

    @PostConstruct
    public void init() {
        if (this.getRunningMode() == IZookeeperService.RUNNING_MODE.STANDALONE) {
            logger.info("The current component is running in standalone mode, so it does not perform " + this.getClass().getSimpleName());
        } else {
            String configCenterStr = this.distributedMutexLockService.getZookeeperService().getZookeeperConfig().getProperty("zookeeper.config.center");
            if (TextUtil.isNotEmpty(configCenterStr) && "true".equals(configCenterStr)) {
                configCenter = true;
                logger.info("Found that the current component configured the configuration center service, and the invalid service scan task was started.");
            } else {
                return;
            }
            this.LOCK_NAME = "invalid-service-scanner-lock";
            this.LOCK_PATH = this.distributedMutexLockService.getZookeeperService().getLockPath() + "/mutex-lock/invalid-service-scanner-lock";
            this.MAX_DELAY_SECONDS = 600;
            this.random = new Random();
        }
    }

    @Scheduled(
            cron = "0 0/30 * * * ?"
    )
    @Async
    public void run() {
        if (this.getRunningMode() == IZookeeperService.RUNNING_MODE.STANDALONE) {
            logger.info("The current component is running in standalone mode, so it does not perform " + this.getClass().getSimpleName());
        } else {
            if (configCenter) {
                int delay = this.random.nextInt(this.MAX_DELAY_SECONDS);
                logger.info("Invalid service scan task is ready for execution after " + delay + "S.");

                try {
                    Thread.sleep((long) delay * 1000L);
                } catch (InterruptedException e) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
                }

                this.distributedMutexLockService.definedOrUse(this.LOCK_NAME, this.LOCK_PATH);
                if (this.distributedMutexLockService.acquire(this.LOCK_NAME)) {
                    try {
                        IZookeeperService zookeeperService = this.distributedMutexLockService.getZookeeperService();
                        List<String> list = this.distributedMutexLockService.getZookeeperService().getInvalidService();
                        if (list != null && list.size() > 0) {
                            logger.info("Found " + list.size() + " invalid services: " + list + "，begin to delete");
                            for (String path : list) {
                                logger.info("Deleting invalid service " + path);
                                zookeeperService.deletePath(path);
                                logger.info("Invalid service " + path + " deleted");
                            }
                            logger.info("All invalid services were deleted successfully.");
                        } else {
                            logger.info("Invalid service not deleted");
                        }
                        logger.info("At present, the Zookeeper service tree is as follows：\n" + zookeeperService.getTreeInfoAsString(zookeeperService.getRootPath()));
                    } catch (Exception e) {
                        throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
                    } finally {
                        this.distributedMutexLockService.release(this.LOCK_NAME);
                    }
                }
            }
        }
    }

    private IZookeeperService.RUNNING_MODE getRunningMode() {
        return this.distributedMutexLockService.getZookeeperService().getRunningMode();
    }
}
