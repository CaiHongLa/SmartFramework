package cn.cloudwalk.smartframework.common.exception.desc;


import cn.cloudwalk.smartframework.common.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public abstract class BaseExceptionDesc {
    private String respCode;
    private String respDesc;
    private Throwable throwable;

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

    public Throwable getThrowable() {
        return this.throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    protected Map<String, Object> getBaseSerializedData() {
        Map<String, Object> data = new HashMap<>();
        data.put("respCode", this.respCode);
        data.put("respDesc", this.respDesc);
        return data;
    }

    public abstract Map<String, Object> getSerializedData();

    public String toJson() {
        return JsonUtil.object2Json(this.getSerializedData());
    }
}
