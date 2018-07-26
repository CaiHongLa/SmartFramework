package cn.cloudwalk.smartframework.common.exception;

/**
 * @author LIYANHUI
 */
public class BusinessExceptionEvent extends ExceptionEvent {
    public BusinessExceptionEvent(Object source) {
        super(source);
    }

    @Override
    public String defCode() {
        return "_BUSINESS_EXCEPTION";
    }
}
