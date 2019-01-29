package cn.cloudwalk.smartframework.common.util.http;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.HttpException;

/**
 * @author LIYANHUI
 */
public class ErrorHttpStatusException extends HttpException {
    public ErrorHttpStatusException(SystemExceptionDesc desc) {
        super(desc);
    }

    @Override
    public String defShortName() {
        return "http status exception";
    }
}
