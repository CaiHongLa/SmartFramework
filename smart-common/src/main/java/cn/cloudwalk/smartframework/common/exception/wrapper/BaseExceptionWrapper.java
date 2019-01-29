package cn.cloudwalk.smartframework.common.exception.wrapper;

import cn.cloudwalk.smartframework.common.exception.desc.BaseExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.desc.impl.BusinessExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.desc.impl.ProtocolExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.util.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author LIYANHUI
 */
public abstract class BaseExceptionWrapper implements IExceptionWrapper {
    protected Logger logger = LogManager.getLogger(this.getClass());
    protected Properties properties;


    @Override
    public Properties getConfigProperties() {
        return this.properties;
    }

    @Override
    public void setConfigProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Object wrapUnexpectedException(BaseExceptionDesc desc) {
        Map<String, Object> data = new HashMap<>();
        data.put("success", false);
        data.put("respDesc", "unknown exception type，should be one of  SystemExceptionDesc,BusinessExceptionDesc,ProtocolExceptionDesc");
        return data;
    }

    @Override
    public Object getWrappedResult(BaseExceptionDesc desc) {
        if (desc instanceof SystemExceptionDesc) {
            return this.wrapSystemException(desc.getSerializedData());
        } else if (desc instanceof BusinessExceptionDesc) {
            return this.wrapBusinessException(desc.getSerializedData());
        } else {
            return desc instanceof ProtocolExceptionDesc ? this.wrapProtocolException(desc.getSerializedData()) : this.wrapUnexpectedException(desc);
        }
    }

    @Override
    public String getWrappedResultAsJson(BaseExceptionDesc desc) {
        return JsonUtil.object2Json(this.getWrappedResult(desc));
    }
}
