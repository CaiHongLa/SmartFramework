package cn.cloudwalk.smartframework.common.exception.exception;

import cn.cloudwalk.smartframework.common.exception.BaseException;
import cn.cloudwalk.smartframework.common.exception.ExceptionType;
import cn.cloudwalk.smartframework.common.exception.desc.impl.ProtocolExceptionDesc;

/**
 * @author LIYANHUI
 */
public abstract class ProtocolException extends BaseException {
    public ProtocolException(ProtocolExceptionDesc desc) {
        super(desc);
    }

    @Override
    public ProtocolExceptionDesc getDesc() {
        return (ProtocolExceptionDesc) super.getDesc();
    }

    @Override
    public ExceptionType defExceptionType() {
        return ExceptionType.PROTOCOL_EXCEPTION;
    }

    @Override
    public String defShortName() {
        return "Protocol Exception";
    }
}
