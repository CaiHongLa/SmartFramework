package cn.cloudwalk.smartframework.common.util.http.bean;

import cn.cloudwalk.smartframework.common.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class ProtocolOutResponse {

    private Boolean success;
    private String respCode;
    private String time;
    private String respDesc;
    private Object data;
    private String responseText;
    private ProtocolOutResponseMetadata metadata;

    public ProtocolOutResponse() {
    }

    public ProtocolOutResponse(String responseText) {
        this.responseText = responseText;
    }

    public ProtocolOutResponse(Boolean success, String respCode, String time, String respDesc, Object data) {
        this.success = success;
        this.respCode = respCode;
        this.time = time;
        this.respDesc = respDesc;
        this.data = data;
    }

    public Boolean getSuccess() {
        return this.success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    public Object getData() {
        return data;
    }

    public Object getRawData() {
        return this.data;
    }

    public Map<String, Object> getDataAsMap() {
        return JsonUtil.json2Map(JsonUtil.object2Json(this.data));
    }

    public List<Object> getDataAsList() {
        return JsonUtil.json2List(JsonUtil.object2Json(this.data));
    }

    public <T> T getDataAsAnyType(Type type) {
        return JsonUtil.json2AnyType(JsonUtil.object2Json(this.data), type);
    }

    public <T> T getDataAs(Class<T> type) {
        return JsonUtil.json2Object(JsonUtil.object2Json(this.data), type);
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ProtocolOutResponseMetadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(ProtocolOutResponseMetadata metadata) {
        this.metadata = metadata;
    }

    public String getResponseText() {
        return this.responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    @Override
    public String toString() {
        return "ProtocolOutResponse [success=" + this.success + ", respCode=" + this.respCode + ", time=" + this.time + ", respDesc=" + this.respDesc + "]";
    }
}
