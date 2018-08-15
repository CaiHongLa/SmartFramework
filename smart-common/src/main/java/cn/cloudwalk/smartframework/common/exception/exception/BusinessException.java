package cn.cloudwalk.smartframework.common.exception.exception;

import cn.cloudwalk.smartframework.common.exception.BaseException;
import cn.cloudwalk.smartframework.common.exception.ExceptionType;
import cn.cloudwalk.smartframework.common.exception.desc.impl.BusinessExceptionDesc;

/**
 * @author LIYANHUI
 */
public abstract class BusinessException extends BaseException {
    public BusinessException(BusinessExceptionDesc desc) {
        super(desc);
    }

    @Override
    public BusinessExceptionDesc getDesc() {
        return (BusinessExceptionDesc) super.getDesc();
    }

    @Override
    public ExceptionType defExceptionType() {
        return ExceptionType.BUSINESS_EXCEPTION;
    }

    @Override
    public String defShortName() {
        return "Business Exception";
    }
}
