package cn.cloudwalk.smartframework.rpc.bootstrap;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.IBaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperRegister;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperWatcher;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 分布式服务启动。
 * <p>
 * 在web容器启动完成后注册服务。在web容器关闭后断开连接。
 *
 * @author 李延辉
 * @see ApplicationListener
 * @see ApplicationContextEvent
 * @since 1.0.0
 */
@Component
public class DistributedBootStrap extends BaseComponent implements IBaseComponent, ApplicationListener<ApplicationContextEvent> {
    private static final Logger logger = LogManager.getLogger(DistributedBootStrap.class);

    @Autowired
    @Qualifier("zookeeperService")
    private IZookeeperService zookeeperService;

    @Autowired
    @Qualifier("zookeeperServiceRegister")
    private IZookeeperRegister zookeeperRegister;

    /**
     * 分布式服务中不能用127.0.0.1 和 localhost，否则无法调用服务
     */
    private List<String> LOCAL_IP_LIST = Arrays.asList("127.0.0.1", "localhost");

    /**
     * 连接zookeeper服务，并设置状态监听器 来断开或注册服务
     */
    private void connect() {
        Properties config = this.zookeeperService.getZookeeperConfig();
        if (this.zookeeperService.getRunningMode() == IZookeeperService.RUNNING_MODE.STANDALONE) {
            logger.info("当前组件以 standalone 模式运行，因此无需连接 zookeeper 集群");
        } else {
            logger.info("当前组件以 distributed 模式运行，即将开始连接 zookeeper 集群");
            if (this.LOCAL_IP_LIST.contains((this.zookeeperService.getLocalIp() + "").trim().toLowerCase())) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("zookeeper 客户端拒绝以 " + this.LOCAL_IP_LIST + " 模式注册服务，请配置内网 IP"));
            } else {
                this.zookeeperService.connect(
                        config.getProperty("zookeeper.url"),
                        config.getProperty("zookeeper.sessionTimeout", "30000"),
                        config.getProperty("zookeeper.connectionTimeout", "30000"), state -> {
                            logger.info("zookeeper 集群状态发生变更：" + state);
                            if (state == IZookeeperWatcher.ConnectionState.CONNECTED || state == IZookeeperWatcher.ConnectionState.RECONNECTED) {
                                zookeeperRegister.registerService();
                            }
                        });
            }
        }
    }

    /**
     * 断开与zookeeper的连接
     */
    private void disconnect() {
        this.zookeeperService.disconnect();
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        //只在Root WebApplicationContext容器加载完毕时执行 防止多次执行
        if (event.getApplicationContext().getParent() == null) {
            if (event instanceof ContextRefreshedEvent) {
                this.connect();
            } else if (event instanceof ContextClosedEvent) {
                this.disconnect();
            }
        }
    }

}
