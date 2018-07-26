package cn.cloudwalk.smartframework.common.exception;

import cn.cloudwalk.smartframework.common.exception.desc.BaseExceptionDesc;

/**
 * @author LIYANHUI
 */
public abstract class BaseException extends RuntimeException {

    private BaseExceptionDesc desc;

    public BaseException(BaseExceptionDesc desc) {
        this.desc = desc;
    }

    public BaseExceptionDesc getDesc() {
        return this.desc;
    }

    public void setDesc(BaseExceptionDesc desc) {
        this.desc = desc;
    }

    public abstract String defShortName();

    public abstract ExceptionType defExceptionType();

    @Override
    public String getMessage() {
        return this.desc.toJson();
    }
}
