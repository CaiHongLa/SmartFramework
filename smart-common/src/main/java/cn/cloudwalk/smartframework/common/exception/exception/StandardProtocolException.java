package cn.cloudwalk.smartframework.common.exception.exception;

import cn.cloudwalk.smartframework.common.exception.desc.impl.ProtocolExceptionDesc;

/**
 * @author LIYANHUI
 */
public class StandardProtocolException extends ProtocolException {
    public StandardProtocolException(ProtocolExceptionDesc desc) {
        super(desc);
    }

    @Override
    public String defShortName() {
        return "标准协议异常";
    }
}
