package cn.cloudwalk.smartframework.common.exception.exception;

import cn.cloudwalk.smartframework.common.exception.desc.impl.ProtocolExceptionDesc;

/**
 * @author LIYANHUI
 */
public class InvalidProtocolParamsException extends InvalidProtocolException {
    public InvalidProtocolParamsException(ProtocolExceptionDesc desc) {
        super(desc);
    }

    @Override
    public String defShortName() {
        return "Invalid Protocol Params Exception";
    }
}
