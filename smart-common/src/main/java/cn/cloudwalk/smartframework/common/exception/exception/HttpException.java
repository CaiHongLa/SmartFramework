package cn.cloudwalk.smartframework.common.exception.exception;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;

/**
 * @author LIYANHUI
 */
public abstract class HttpException extends SystemException {
    public HttpException(SystemExceptionDesc desc) {
        super(desc);
    }

    @Override
    public String defShortName() {
        return "Http Exception";
    }
}
