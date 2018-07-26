package cn.cloudwalk.smartframework.common.exception.desc.impl;

import cn.cloudwalk.smartframework.common.exception.desc.BaseExceptionDesc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class BusinessExceptionDesc extends BaseExceptionDesc {
    private BusinessExceptionDesc.SHOW_TYPE showType;

    public BusinessExceptionDesc(String code, BusinessExceptionDesc.SHOW_TYPE showType, String message) {
        this.setRespCode(code);
        this.showType = showType;
        this.setRespDesc(message);
    }

    public BusinessExceptionDesc.SHOW_TYPE getShowType() {
        return this.showType;
    }

    public void setShowType(BusinessExceptionDesc.SHOW_TYPE showType) {
        this.showType = showType;
    }

    @Override
    public Map<String, Object> getSerializedData() {
        Map<String, Object> data = new HashMap<>();
        data.put("showType", this.showType);
        data.put("exceptionType", "BUSINESS");
        data.putAll(this.getBaseSerializedData());
        return data;
    }

    public enum SHOW_TYPE {
        NONE("none"),
        INFO("info"),
        WARN("warn"),
        ERROR("error");

        private String value;

        SHOW_TYPE(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
