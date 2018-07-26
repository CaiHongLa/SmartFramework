package cn.cloudwalk.smartframework.common.util.http.bean;

import cn.cloudwalk.smartframework.common.protocol.ProtocolIn;
import cn.cloudwalk.smartframework.common.util.ReflectUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class ProtocolHttpRequest {
    private String code;
    private String serviceUrl;
    private HTTP_CONTENT_TRANSFER_TYPE transferType;
    private Object params;
    private Map<String, Object> options;

    public ProtocolHttpRequest() {
    }

    public ProtocolHttpRequest(String code, String serviceUrl, ProtocolIn params, HTTP_CONTENT_TRANSFER_TYPE transferType) {
        this.code = code;
        this.serviceUrl = serviceUrl;
        this.params = params;
        this.transferType = transferType;
    }

    public ProtocolHttpRequest(String serviceUrl, HTTP_CONTENT_TRANSFER_TYPE transferType) {
        this.serviceUrl = serviceUrl;
        this.transferType = transferType;
    }

    public ProtocolHttpRequest(String serviceUrl, Map<String, Object> params, HTTP_CONTENT_TRANSFER_TYPE transferType) {
        this.serviceUrl = serviceUrl;
        this.params = params;
        this.transferType = transferType;
    }

    public ProtocolHttpRequest(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType) {
        this.serviceUrl = serviceUrl;
        this.params = protocol;
        this.transferType = transferType;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public HTTP_CONTENT_TRANSFER_TYPE getTransferType() {
        return this.transferType;
    }

    public void setTransferType(HTTP_CONTENT_TRANSFER_TYPE transferType) {
        this.transferType = transferType;
    }

    public Object getParams() {
        return this.params;
    }

    public void setParams(ProtocolIn params) {
        this.params = ReflectUtil.getPropertyValues(params);
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void addOption(String key, Object value) {
        if (this.options == null) {
            this.options = new HashMap<>();
        }

        this.options.put(key, value);
    }

    public Object getOption(String key) {
        return this.options == null ? null : this.options.get(key);
    }

    public <T> T getOptionAs(String key, Class<T> type) {
        Object value = this.getOption(key);
        return value == null ? null : (T) value;
    }

    public Map<String, Object> getOptions() {
        return this.options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "ProtocolHttpRequest [code=" + this.code + ", serviceUrl=" + this.serviceUrl + ", transferType=" + this.transferType + ", params=" + this.params + ", options=" + this.options + "]";
    }
}
