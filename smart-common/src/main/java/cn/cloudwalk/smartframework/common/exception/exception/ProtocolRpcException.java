package cn.cloudwalk.smartframework.common.exception.exception;


import cn.cloudwalk.smartframework.common.exception.ExceptionType;
import cn.cloudwalk.smartframework.common.exception.desc.impl.ProtocolExceptionDesc;

/**
 * @author LIYANHUI
 */
public class ProtocolRpcException extends ProtocolException {
    public ProtocolRpcException(ProtocolExceptionDesc desc) {
        super(desc);
    }

    @Override
    public ExceptionType defExceptionType() {
        return ExceptionType.PROTOCOL_OF_RPC_EXCEPTION;
    }

    @Override
    public String defShortName() {
        return "Protocol Rpc Exception";
    }
}
