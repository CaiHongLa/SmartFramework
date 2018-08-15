package cn.cloudwalk.smartframework.common.exception.exception;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;

/**
 * @author LIYANHUI
 */
public class FrameworkInternalSystemException extends SystemException {
    public FrameworkInternalSystemException(SystemExceptionDesc desc) {
        super(desc);
    }

    @Override
    public String defShortName() {
        return "Framework Exception";
    }
}
