package cn.cloudwalk.smartframework.transport.support.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 事件异常处理器
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/5/4 17:13
 * @since 2.0.0
 */
public class ChannelEventExceptionHandler implements ExceptionHandler<ChannelEvent> {

    private static final Logger logger = LogManager.getLogger(ChannelEventExceptionHandler.class);

    @Override
    public void handleEventException(Throwable ex, long sequence, ChannelEvent event) {
        logger.error(ex);
        event.executeException(ex, sequence, event);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        logger.error(ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        logger.error(ex);
    }
}
