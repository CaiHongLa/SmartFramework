package cn.cloudwalk.smartframework.common.exception.exception;


import cn.cloudwalk.smartframework.common.exception.BaseException;
import cn.cloudwalk.smartframework.common.exception.ExceptionType;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;

/**
 * @author LIYANHUI
 */
public abstract class SystemException extends BaseException {

    public SystemException(SystemExceptionDesc desc) {
        super(desc);
    }

    @Override
    public SystemExceptionDesc getDesc() {
        return (SystemExceptionDesc) super.getDesc();
    }

    @Override
    public ExceptionType defExceptionType() {
        return ExceptionType.SYSTEM_EXCEPTION;
    }

    @Override
    public String defShortName() {
        return "System Exception";
    }
}
