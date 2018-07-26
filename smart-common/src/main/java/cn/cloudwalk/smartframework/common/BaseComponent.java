package cn.cloudwalk.smartframework.common;

import cn.cloudwalk.smartframework.common.event.BaseEvent;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.task.IBackGroundTaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;

/**
 * 基础组件
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class BaseComponent implements IBaseComponent {

    protected final Logger logger = LogManager.getLogger(getClass());

    @Autowired(
            required = false
    )
    @Qualifier("configurationService")
    private IConfigurationService configurationService;

    @Autowired(
            required = false
    )
    @Qualifier("backGroundTaskService")
    private IBackGroundTaskService backGroundTaskService;

    private ApplicationContext applicationContext;
    private ServletContext servletContext;

    @Override
    public void publishEvent(BaseEvent event) {
        applicationContext.publishEvent(event);
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public IConfigurationService getConfigurationService() {
        if (configurationService == null) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("IConfigurationService服务不可用，请导入Config组件！"));
        }
        return configurationService;
    }

    public IBackGroundTaskService getBackGroundTaskService() {
        if (backGroundTaskService == null) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("IBackGroundTaskService服务不可用，请导入Task组件！"));
        }
        return backGroundTaskService;
    }
}
