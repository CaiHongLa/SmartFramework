package cn.cloudwalk.smartframework.common;

import cn.cloudwalk.smartframework.common.event.BaseEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * @author LIYANHUI
 */
public interface IBaseComponent extends ApplicationContextAware, ServletContextAware {

    /**
     * 引入spring的事件发布机制
     *
     * @param event 需要发布的事件{@link BaseEvent}
     */
    void publishEvent(BaseEvent event);

    ApplicationContext getApplicationContext();

    ServletContext getServletContext();
}
