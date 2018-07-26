package cn.cloudwalk.smartframework.task.destroy;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.util.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * 资源注销服务
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("resourceDestroy")
public class ResourceDestroy extends BaseComponent implements ApplicationListener<ContextClosedEvent> {
    private static final Logger logger = LogManager.getLogger(ResourceDestroy.class);

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if(event.getApplicationContext().getParent() == null) {
            logger.info("开始关闭Http连接池！");
            HttpUtil.Async.closeAsyncHttpClient();
            HttpUtil.Sync.closeHttpClient();
            logger.info("Http连接池关闭完成！");
        }
    }
}
