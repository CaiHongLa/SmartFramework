package cn.cloudwalk.smartframework.common.exception.wrapper;

import cn.cloudwalk.smartframework.common.exception.desc.BaseExceptionDesc;

import java.util.Map;
import java.util.Properties;

/**
 * @author LIYANHUI
 */
public interface IExceptionWrapper {
    Properties getConfigProperties();

    void setConfigProperties(Properties properties);

    Object wrapSystemException(Map<String, Object> serializedData);

    Object wrapBusinessException(Map<String, Object> serializedData);

    Object wrapProtocolException(Map<String, Object> serializedData);

    Object wrapUnexpectedException(BaseExceptionDesc desc);

    Object getWrappedResult(BaseExceptionDesc desc);

    String getWrappedResultAsJson(BaseExceptionDesc desc);
}
