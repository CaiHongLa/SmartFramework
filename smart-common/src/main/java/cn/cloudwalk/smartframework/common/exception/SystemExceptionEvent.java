package cn.cloudwalk.smartframework.common.exception;

/**
 * @author LIYANHUI
 */
public class SystemExceptionEvent extends ExceptionEvent {
    public SystemExceptionEvent(Object source) {
        super(source);
    }

    @Override
    public String defCode() {
        return "_SYSTEM_EXCEPTION";
    }
}
