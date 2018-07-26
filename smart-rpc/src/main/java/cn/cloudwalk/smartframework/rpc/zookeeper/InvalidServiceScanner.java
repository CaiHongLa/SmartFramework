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
            logger.info("当前组件以 standalone 模式运行，因此不执行 " + this.getClass().getSimpleName());
        } else {
            String configCenterStr = this.distributedMutexLockService.getZookeeperService().getZookeeperConfig().getProperty("zookeeper.config.center");
            if (TextUtil.isNotEmpty(configCenterStr) && "true".equals(configCenterStr)) {
                configCenter = true;
                logger.info("发现当前组件配置了配置中心服务，已启动无效服务扫描任务");
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
            logger.info("当前组件以 standalone 模式运行，因此此次不执行 " + this.getClass().getSimpleName());
        } else {
            if (configCenter) {
                int delay = this.random.nextInt(this.MAX_DELAY_SECONDS);
                logger.info("无效服务扫描任务准备于 " + delay + "s 后执行");

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
                            logger.info("发现 " + list.size() + " 个无效服务，有 " + list + "，开始删除");
                            for (String path : list) {
                                logger.info("正在删除无效服务 " + path);
                                zookeeperService.deletePath(path);
                                logger.info("无效服务 " + path + " 删除成功");
                            }
                            logger.info("所有无效服务删除成功");
                        } else {
                            logger.info("没有待删除的无效服务");
                        }
                        logger.info("目前Zookeeper服务树如下：\n" + zookeeperService.getTreeInfoAsString(zookeeperService.getRootPath()));
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
