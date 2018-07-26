package cn.cloudwalk.smartframework.common.util.http.bean;

import cn.cloudwalk.smartframework.common.model.IDataModel;
import cn.cloudwalk.smartframework.common.util.JsonUtil;
import org.apache.http.Header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class HttpRequest {

    private String code;
    private String url;
    private HTTP_METHOD method;
    private HTTP_CONTENT_TRANSFER_TYPE transferType;
    private Object params;
    private List<? extends Header> headers;
    private Map<String, Object> options;
    private String response;

    public HttpRequest() {
    }

    public HttpRequest(String url) {
        this.url = url;
        this.method = HTTP_METHOD.POST;
    }

    public HttpRequest(String url, Object params) {
        this.url = url;
        this.method = HTTP_METHOD.POST;
        this.params = params;
    }

    public HttpRequest(String url, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType) {
        this.url = url;
        this.method = HTTP_METHOD.POST;
        this.params = params;
        this.transferType = transferType;
    }

    public HttpRequest(String code, String url) {
        this.code = code;
        this.url = url;
        this.method = HTTP_METHOD.POST;
    }

    public HttpRequest(String code, String url, Object params) {
        this.code = code;
        this.url = url;
        this.method = HTTP_METHOD.POST;
        this.params = params;
    }

    public HttpRequest(String code, String url, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType) {
        this.code = code;
        this.url = url;
        this.method = HTTP_METHOD.POST;
        this.params = params;
        this.transferType = transferType;
    }

    public HttpRequest(String url, HTTP_METHOD method) {
        this.url = url;
        this.method = method;
    }

    public HttpRequest(String code, String url, HTTP_METHOD method) {
        this.code = code;
        this.url = url;
        this.method = method;
    }

    public HttpRequest(String url, HTTP_METHOD method, Object params) {
        this.url = url;
        this.method = method;
        this.params = params;
    }

    public HttpRequest(String url, HTTP_METHOD method, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType) {
        this.url = url;
        this.method = method;
        this.params = params;
        this.transferType = transferType;
    }

    public HttpRequest(String code, String url, HTTP_METHOD method, Object params) {
        this.code = code;
        this.url = url;
        this.method = method;
        this.params = params;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HTTP_METHOD getMethod() {
        return this.method;
    }

    public void setMethod(HTTP_METHOD method) {
        this.method = method;
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

    public void setParams(IDataModel params) {
        this.params = params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<? extends Header> getHeaders() {
        return this.headers;
    }

    public void setHeaders(List<? extends Header> headers) {
        this.headers = headers;
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
        return (T) value;
    }

    public Map<String, Object> getOptions() {
        return this.options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "HttpRequest [code=" + this.code + ", url=" + this.url + ", method=" + this.method + ", transferType=" + this.transferType + ", params=" +
                JsonUtil.object2Json(this.params) + ", headers=" + this.headers + ", options=" + this.options + "]";
    }
}
