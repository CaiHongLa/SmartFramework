package cn.cloudwalk.smartframework.common.exception.exception;

import cn.cloudwalk.smartframework.common.exception.ExceptionType;
import cn.cloudwalk.smartframework.common.exception.desc.impl.ProtocolExceptionDesc;

/**
 * @author LIYANHUI
 */
public class IgnorableProtocolException extends ProtocolException {
    public IgnorableProtocolException(ProtocolExceptionDesc desc) {
        super(desc);
    }

    @Override
    public ExceptionType defExceptionType() {
        return ExceptionType.PROTOCOL_OF_IGNORABLE_EXCEPTION;
    }

    @Override
    public String defShortName() {
        return "可忽略的协议异常";
    }
}
