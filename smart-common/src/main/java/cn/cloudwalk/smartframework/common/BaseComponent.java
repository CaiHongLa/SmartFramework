package cn.cloudwalk.smartframework.common;

import cn.cloudwalk.smartframework.common.distributed.IRemoteService;
import cn.cloudwalk.smartframework.common.distributed.IRpcInvokeService;
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

    protected static final String FRAMEWORK_BANNER =
                    "\n                                                                                                                                                                               \n" +
                    "                                                                                                                                                                                 \n" +
                    "                                                                                                                                                                                 \n" +
                    "                                                                                                                                                                                 \n" +
                    " ▄▄▄▄▄▄▄▄▄▄▄  ▄▄       ▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄       ▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄         ▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄    ▄ \n" +
                    "▐░░░░░░░░░░░▌▐░░▌     ▐░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░▌     ▐░░▌▐░░░░░░░░░░░▌▐░▌       ▐░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░▌  ▐░▌\n" +
                    "▐░█▀▀▀▀▀▀▀▀▀ ▐░▌░▌   ▐░▐░▌▐░█▀▀▀▀▀▀▀█░▌▐░█▀▀▀▀▀▀▀█░▌ ▀▀▀▀█░█▀▀▀▀ ▐░█▀▀▀▀▀▀▀▀▀ ▐░█▀▀▀▀▀▀▀█░▌▐░█▀▀▀▀▀▀▀█░▌▐░▌░▌   ▐░▐░▌▐░█▀▀▀▀▀▀▀▀▀ ▐░▌       ▐░▌▐░█▀▀▀▀▀▀▀█░▌▐░█▀▀▀▀▀▀▀█░▌▐░▌ ▐░▌ \n" +
                    "▐░▌          ▐░▌▐░▌ ▐░▌▐░▌▐░▌       ▐░▌▐░▌       ▐░▌     ▐░▌     ▐░▌          ▐░▌       ▐░▌▐░▌       ▐░▌▐░▌▐░▌ ▐░▌▐░▌▐░▌          ▐░▌       ▐░▌▐░▌       ▐░▌▐░▌       ▐░▌▐░▌▐░▌  \n" +
                    "▐░█▄▄▄▄▄▄▄▄▄ ▐░▌ ▐░▐░▌ ▐░▌▐░█▄▄▄▄▄▄▄█░▌▐░█▄▄▄▄▄▄▄█░▌     ▐░▌     ▐░█▄▄▄▄▄▄▄▄▄ ▐░█▄▄▄▄▄▄▄█░▌▐░█▄▄▄▄▄▄▄█░▌▐░▌ ▐░▐░▌ ▐░▌▐░█▄▄▄▄▄▄▄▄▄ ▐░▌   ▄   ▐░▌▐░▌       ▐░▌▐░█▄▄▄▄▄▄▄█░▌▐░▌░▌   \n" +
                    "▐░░░░░░░░░░░▌▐░▌  ▐░▌  ▐░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌     ▐░▌     ▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░▌  ▐░▌  ▐░▌▐░░░░░░░░░░░▌▐░▌  ▐░▌  ▐░▌▐░▌       ▐░▌▐░░░░░░░░░░░▌▐░░▌    \n" +
                    " ▀▀▀▀▀▀▀▀▀█░▌▐░▌   ▀   ▐░▌▐░█▀▀▀▀▀▀▀█░▌▐░█▀▀▀▀█░█▀▀      ▐░▌     ▐░█▀▀▀▀▀▀▀▀▀ ▐░█▀▀▀▀█░█▀▀ ▐░█▀▀▀▀▀▀▀█░▌▐░▌   ▀   ▐░▌▐░█▀▀▀▀▀▀▀▀▀ ▐░▌ ▐░▌░▌ ▐░▌▐░▌       ▐░▌▐░█▀▀▀▀█░█▀▀ ▐░▌░▌   \n" +
                    "          ▐░▌▐░▌       ▐░▌▐░▌       ▐░▌▐░▌     ▐░▌       ▐░▌     ▐░▌          ▐░▌     ▐░▌  ▐░▌       ▐░▌▐░▌       ▐░▌▐░▌          ▐░▌▐░▌ ▐░▌▐░▌▐░▌       ▐░▌▐░▌     ▐░▌  ▐░▌▐░▌  \n" +
                    " ▄▄▄▄▄▄▄▄▄█░▌▐░▌       ▐░▌▐░▌       ▐░▌▐░▌      ▐░▌      ▐░▌     ▐░▌          ▐░▌      ▐░▌ ▐░▌       ▐░▌▐░▌       ▐░▌▐░█▄▄▄▄▄▄▄▄▄ ▐░▌░▌   ▐░▐░▌▐░█▄▄▄▄▄▄▄█░▌▐░▌      ▐░▌ ▐░▌ ▐░▌ \n" +
                    "▐░░░░░░░░░░░▌▐░▌       ▐░▌▐░▌       ▐░▌▐░▌       ▐░▌     ▐░▌     ▐░▌          ▐░▌       ▐░▌▐░▌       ▐░▌▐░▌       ▐░▌▐░░░░░░░░░░░▌▐░░▌     ▐░░▌▐░░░░░░░░░░░▌▐░▌       ▐░▌▐░▌  ▐░▌\n" +
                    " ▀▀▀▀▀▀▀▀▀▀▀  ▀         ▀  ▀         ▀  ▀         ▀       ▀       ▀            ▀         ▀  ▀         ▀  ▀         ▀  ▀▀▀▀▀▀▀▀▀▀▀  ▀▀       ▀▀  ▀▀▀▀▀▀▀▀▀▀▀  ▀         ▀  ▀    ▀ \n" +
                    "                                                                                                                                                                                 \n" +
                    "                                                                                                                                                                                 \n" +
                    "                                                                                                                                                                                 \n" +
                    "                                                                                                                                                           on version  2.0.20      ";

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

    @Autowired(required = false)
    @Qualifier("remoteService")
    private IRemoteService remoteService;

    @Autowired(required = false)
    @Qualifier("rpcInvokeService")
    private IRpcInvokeService rpcInvokeService;

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
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("IConfigurationService is null ，please import Config module in your application context！"));
        }
        return configurationService;
    }

    public IBackGroundTaskService getBackGroundTaskService() {
        if (backGroundTaskService == null) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("IBackGroundTaskService is null ，please import Task module in your application context！"));
        }
        return backGroundTaskService;
    }

    public IRemoteService getRemoteService() {
        if(null == remoteService){
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("IRemoteService is null ，please import Rpc module in your application context！"));
        }
        return remoteService;
    }

    public IRpcInvokeService getRpcInvokeService() {
        if(null == rpcInvokeService){
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("IRpcInvokeService is null ，please import Rpc module in your application context！"));
        }
        return rpcInvokeService;
    }
}
