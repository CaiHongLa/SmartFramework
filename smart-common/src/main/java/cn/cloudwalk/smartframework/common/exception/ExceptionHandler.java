package cn.cloudwalk.smartframework.common.exception;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.IBaseComponent;
import cn.cloudwalk.smartframework.common.exception.desc.BaseExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.desc.impl.BusinessExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.desc.impl.ProtocolExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.BusinessException;
import cn.cloudwalk.smartframework.common.exception.exception.ProtocolException;
import cn.cloudwalk.smartframework.common.exception.exception.SystemException;
import cn.cloudwalk.smartframework.common.exception.wrapper.IExceptionWrapper;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

/**
 * @author LIYANHUI
 */
public class ExceptionHandler extends BaseComponent implements HandlerExceptionResolver, Ordered, IBaseComponent {
    private IExceptionWrapper exceptionWrapper;
    private Integer httpStatus;
    private static final Logger logger = LogManager.getLogger(ExceptionHandler.class);

    @PostConstruct
    private void init() {
        Properties config = getConfigurationService().getApplicationCfg();
        this.httpStatus = Integer.valueOf(config.getProperty("system.exceptionHttpStatus", "500"));
        if (this.httpStatus == 200) {
            logger.info("请注意：配置了 system.exceptionHttpStatus=200，因此所有的异常发生时，http status 均为 200");
        }

        String className = config.getProperty("system.exceptionWrapper", "cn.cloudwalk.smartframework.common.exception.wrapper.DefaultExceptionWrapper");

        try {
            this.exceptionWrapper = (IExceptionWrapper) Class.forName(className).newInstance();
            this.exceptionWrapper.setConfigProperties(config);
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var3) {
            logger.error(var3);
        }

        if (config.containsKey("system.exceptionWrapper")) {
            logger.info("检测到 system.exceptionWrapper 包装器配置，已自动启用 " + config.getProperty("system.exceptionWrapper") + " 包装器");
        } else {
            logger.info("没有检测到 system.exceptionWrapper 包装器配置，已自动启用默认包装器 cn.cloudwalk.smartframework.common.exception.wrapper.DefaultExceptionWrapper");
        }

    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        return this.processException(request, response, handler, e);
    }

    private ModelAndView processException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        BaseExceptionDesc targetDesc = null;
        SystemException exception;
        SystemExceptionDesc desc;
        if (e instanceof SystemException) {
            exception = (SystemException) e;
            desc = TextUtil.getDeepestSystemExceptionDesc(exception);
            targetDesc = desc;
            logger.error(desc, desc.getThrowable());
        } else if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            BusinessExceptionDesc businessExceptionDesc = businessException.getDesc();
            businessExceptionDesc.setThrowable(e);
            targetDesc = businessExceptionDesc;
            logger.error(businessExceptionDesc, e);
        } else if (e instanceof ProtocolException) {
            ProtocolException protocolException = (ProtocolException) e;
            ProtocolExceptionDesc protocolExceptionDesc = protocolException.getDesc();
            protocolExceptionDesc.setThrowable(e);
            targetDesc = protocolExceptionDesc;
            logger.error(protocolExceptionDesc, e);
        } else {
            exception = SystemExceptionDesc.convertFromNativeException(e);
            desc = exception.getDesc();
            targetDesc = desc;
            logger.error(desc, desc.getThrowable());
        }

        if (targetDesc instanceof SystemExceptionDesc) {
            this.publishEvent(new SystemExceptionEvent(targetDesc));
        } else if (targetDesc instanceof BusinessExceptionDesc) {
            this.publishEvent(new BusinessExceptionEvent(targetDesc));
        }

        try {
            response.setContentType("text/json");
            response.getOutputStream().write(this.exceptionWrapper.getWrappedResultAsJson(targetDesc).getBytes("UTF-8"));
        } catch (Exception ee) {
            logger.error(ee);
        }

        return this.httpStatus == 200 ? new ModelAndView() : null;
    }

    @Override
    public int getOrder() {
        return -2147483648;
    }
}
