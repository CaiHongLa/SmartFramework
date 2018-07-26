package cn.cloudwalk.smartframework.core.controller.builder;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.protocol.ProtocolOut;
import cn.cloudwalk.smartframework.core.result.impl.ProtocolResultModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author LIYANHUI
 */
@Component("protocolBuilder")
public class ProtocolBuilder {

    public ProtocolResultModel build(Object data) {
        this.check(data);
        return new ProtocolResultModel(data);
    }

    public ProtocolResultModel build(Object data, String message) {
        this.check(data);
        return new ProtocolResultModel(data, message);
    }

    public ProtocolResultModel build(Object data, String resultCode, String message) {
        this.check(data);
        return new ProtocolResultModel(data, resultCode, message);
    }

    private void check(Object data) {
        if (data != null && !(data instanceof ProtocolOut) && !(data instanceof Map) && !(data instanceof List) && !(data instanceof Object[])) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("400000", "Protocol Data 类型仅能为 [ProtocolOut子类、Map、List、数组] 中的其中一种"));
        }
    }
}
