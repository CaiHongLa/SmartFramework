package cn.cloudwalk.smartframework.core.result.impl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class ProtocolResultModel extends AbstractJsonResultModel {

    private String respCode;
    private String respDesc;
    private Object data;
    private String NORMAL_CODE = "0000000";

    public ProtocolResultModel(Object data) {
        this.respCode = this.NORMAL_CODE;
        this.data = data;
    }

    public ProtocolResultModel(Object data, String message) {
        this.data = data;
        this.respCode = this.NORMAL_CODE;
        this.respDesc = message;
    }

    public ProtocolResultModel(String resultCode, String message) {
        this.respCode = resultCode;
        this.respDesc = message;
    }

    public ProtocolResultModel(Object data, String resultCode, String message) {
        this.data = data;
        this.respCode = resultCode;
        this.respDesc = message;
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

    @Override
    protected Object getObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("respCode", this.respCode);
        map.put("respDesc", this.respDesc);
        map.put("data", this.data);
        return map;
    }

}
