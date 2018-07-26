package cn.cloudwalk.smartframework.common.model;

import cn.cloudwalk.smartframework.common.util.ReflectUtil;

import java.util.Map;

/**
 * @author LIYANHUI
 */
public class BaseDataModel implements IDataModel {

    @Override
    public void copyPropertiesTo(IDataModel dataModel) {
        ReflectUtil.copyPropertiesFromBean(this, dataModel);
    }

    @Override
    public void copyPropertiesFromBean(IDataModel dataModel) {
        ReflectUtil.copyPropertiesFromBean(dataModel, this);
    }

    @Override
    public void copyPropertiesFromMap(Map<String, Object> map) {
        ReflectUtil.copyPropertiesFromMap(this, map);
    }

    @Override
    public <T extends IDataModel> T createAndCopyProperties(Class<T> t) {
        return ReflectUtil.createAndCopyProperties(this, t);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> bean2Map() {
        return (Map<String, Object>) ReflectUtil.bean2Map(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName())
                .append(":")
                .append(ReflectUtil.getPropertyValues(this));
        return builder.toString();
    }
}
