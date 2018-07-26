package cn.cloudwalk.smartframework.common.exception.exception;


import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;

/**
 * @author LIYANHUI
 */
public abstract class JdbcException extends SystemException {
    public JdbcException(SystemExceptionDesc desc) {
        super(desc);
    }

    @Override
    public String defShortName() {
        return "jdbc 异常";
    }
}
