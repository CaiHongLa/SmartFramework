package cn.cloudwalk.smartframework.common.exception;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;

/**
 * @author LIYANHUI
 */
public class ErrorResultDesc extends BaseDomain {
    private String respCode;
    private String respDesc;

    public ErrorResultDesc(String code, String message) {
        this.respCode = code;
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
}
