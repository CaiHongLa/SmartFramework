package cn.cloudwalk.smartframework.common.exception.desc.impl;

import cn.cloudwalk.smartframework.common.exception.desc.BaseExceptionDesc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class ProtocolExceptionDesc extends BaseExceptionDesc {
    private String respCode;
    private String respDesc;

    public ProtocolExceptionDesc(String resultCode, String message) {
        this.respCode = resultCode;
        this.respDesc = message;
        this.setRespCode(resultCode);
    }

    @Override
    public String getRespCode() {
        return respCode;
    }

    @Override
    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    @Override
    public String getRespDesc() {
        return respDesc;
    }

    @Override
    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    @Override
    public Map<String, Object> getSerializedData() {
        Map<String, Object> data = new HashMap<>();
        data.put("respCode", this.respCode);
        data.put("respDesc", this.respDesc);
        return data;
    }
}
