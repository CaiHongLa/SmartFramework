package cn.cloudwalk.smartframework.core.result.impl;

import cn.cloudwalk.smartframework.common.util.JsonUtil;
import cn.cloudwalk.smartframework.core.result.AbstractResultModel;

/**
 * @author LIYANHUI
 */
public abstract class AbstractJsonResultModel extends AbstractTextResultModel {

    @Override
    protected String getText() {
        String ret = null;
        Object obj = this.getObject();
        if (obj != null) {
            if (obj instanceof String) {
                ret = obj + "";
            } else {
                ret = JsonUtil.object2Json(obj);
            }
        }
        return ret;
    }

    @Override
    protected ContentType getResultContentType() {
        return AbstractResultModel.ContentType.JSON;
    }

    protected abstract Object getObject();
}
