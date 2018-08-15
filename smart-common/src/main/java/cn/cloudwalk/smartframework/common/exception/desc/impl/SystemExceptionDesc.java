package cn.cloudwalk.smartframework.common.exception.desc.impl;


import cn.cloudwalk.smartframework.common.exception.desc.BaseExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.exception.exception.SystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class SystemExceptionDesc extends BaseExceptionDesc {
    private static final String DEFAULT_MESSAGE = "System Error";

    public SystemExceptionDesc(Throwable throwable) {
        super.setThrowable(throwable);
        this.setRespDesc(DEFAULT_MESSAGE);
    }

    public SystemExceptionDesc(Throwable throwable, String code) {
        super.setThrowable(throwable);
        this.setRespDesc(DEFAULT_MESSAGE);
        this.setRespCode(code);
    }

    public SystemExceptionDesc(String message) {
        this.setRespDesc(message);
    }

    public SystemExceptionDesc(String message, String code) {
        this.setRespDesc(message);
        this.setRespCode(code);
    }

    public SystemExceptionDesc(String message, Throwable throwable) {
        super.setThrowable(throwable);
        this.setRespDesc(message);
    }

    public SystemExceptionDesc(String message, String code, Throwable throwable) {
        super.setThrowable(throwable);
        this.setRespDesc(message);
        this.setRespCode(code);
    }

    public static SystemException convertFromNativeException(Exception e) {
        return new FrameworkInternalSystemException(new SystemExceptionDesc(e));
    }

    @Override
    public Map<String, Object> getSerializedData() {
        Map<String, Object> data = new HashMap<>();
        data.put("exceptionType", "SYSTEM");
        data.putAll(this.getBaseSerializedData());
        return data;
    }
}
