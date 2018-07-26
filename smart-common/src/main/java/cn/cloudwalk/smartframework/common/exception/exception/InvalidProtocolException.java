package cn.cloudwalk.smartframework.common.exception.exception;

import cn.cloudwalk.smartframework.common.exception.ExceptionType;
import cn.cloudwalk.smartframework.common.exception.desc.impl.ProtocolExceptionDesc;

/**
 * @author LIYANHUI
 */
public class InvalidProtocolException extends ProtocolException {
    public InvalidProtocolException(ProtocolExceptionDesc desc) {
        super(desc);
    }

    @Override
    public ExceptionType defExceptionType() {
        return ExceptionType.PROTOCOL_OF_INVALID_EXCEPTION;
    }

    @Override
    public String defShortName() {
        return "无效的协议异常";
    }
}
