package cn.cloudwalk.smartframework.common.protocol.validate;

/**
 * @author LIYANHUI
 */
public class ValidateResultItem {
    private String code;
    private String message;

    public ValidateResultItem(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
