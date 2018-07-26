package cn.cloudwalk.smartframework.common.exception.wrapper;

import cn.cloudwalk.smartframework.common.exception.ExceptionType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class ProtocolExceptionWrapper extends ExceptionWrapperAdapter {
    private static final String DEFAULT_ERROR_CODE_CONFIG_FIELD_NAME = "system.exceptionWrapper.defaultErrorCode";
    private String DEFAULT_ERROR_CODE;

    public ProtocolExceptionWrapper() {
    }

    @Override
    public Object wrapSystemException(Map<String, Object> serializedData) {
        return this.build(ExceptionType.SYSTEM_EXCEPTION, serializedData);
    }

    @Override
    public Object wrapBusinessException(Map<String, Object> serializedData) {
        return this.build(ExceptionType.BUSINESS_EXCEPTION, serializedData);
    }

    private Map<String, Object> build(ExceptionType type, Map<String, Object> serializedData) {
        if (this.DEFAULT_ERROR_CODE == null && this.getConfigProperties().containsKey(DEFAULT_ERROR_CODE_CONFIG_FIELD_NAME)) {
            this.DEFAULT_ERROR_CODE = this.getConfigProperties().getProperty(DEFAULT_ERROR_CODE_CONFIG_FIELD_NAME);
            this.logger.info("已设置 ProtocolExceptionWrapper 的默认错误编码为 " + this.DEFAULT_ERROR_CODE);
        } else {
            this.logger.warn("设置了 ProtocolExceptionWrapper 异常包装器，但没有指定默认错误编码，缺少 system.exceptionWrapper.defaultErrorCode 配置");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("respCode", type == ExceptionType.SYSTEM_EXCEPTION && serializedData.get("respCode") != null ? serializedData.get("respCode") : this.DEFAULT_ERROR_CODE);
        data.put("respDesc", serializedData.get("respDesc"));
        return data;
    }
}
