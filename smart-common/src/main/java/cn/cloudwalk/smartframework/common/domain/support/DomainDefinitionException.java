package cn.cloudwalk.smartframework.common.domain.support;


import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.JdbcException;

/**
 * @author LIYANHUI
 * @since 1.0.0
 */
public class DomainDefinitionException extends JdbcException {
    public DomainDefinitionException(SystemExceptionDesc desc) {
        super(desc);
    }

    @Override
    public String defShortName() {
        return "Domain Define Exception";
    }
}
