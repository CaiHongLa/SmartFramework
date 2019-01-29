package cn.cloudwalk.smartframework.common.exception.exception;

import cn.cloudwalk.smartframework.common.exception.desc.impl.BusinessExceptionDesc;

/**
 * @author LIYANHUI
 */
public class StandardBusinessException extends BusinessException {
    public StandardBusinessException(BusinessExceptionDesc desc) {
        super(desc);
    }

    @Override
    public String defShortName() {
        return "Standard Business Exception";
    }
}
