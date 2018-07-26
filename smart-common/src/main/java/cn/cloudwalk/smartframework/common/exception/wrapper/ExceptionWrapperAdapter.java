package cn.cloudwalk.smartframework.common.exception.wrapper;

import java.util.Map;

/**
 * @author LIYANHUI
 */
public abstract class ExceptionWrapperAdapter extends BaseExceptionWrapper {

    @Override
    public Object wrapSystemException(Map<String, Object> serializedData) {
        return serializedData;
    }

    @Override
    public Object wrapBusinessException(Map<String, Object> serializedData) {
        return serializedData;
    }

    @Override
    public Object wrapProtocolException(Map<String, Object> serializedData) {
        return serializedData;
    }
}
