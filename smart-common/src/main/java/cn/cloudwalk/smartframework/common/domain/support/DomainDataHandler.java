package cn.cloudwalk.smartframework.common.domain.support;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.ReflectUtil;
import cn.cloudwalk.smartframework.common.util.TextUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 实体数据处理
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class DomainDataHandler {

    /**
     * 持有的实体
     */
    private BaseDomain baseDomain;

    /**
     * 实体的属性
     */
    private DomainMetadata metadata;

    public DomainDataHandler(BaseDomain baseDomain) {
        this.baseDomain = baseDomain;
        this.metadata = baseDomain.getMetadata();
    }

    public Object getValue(String propertyName) {
        return ReflectUtil.getPropertyValue(propertyName, this.baseDomain);
    }

    public void setValue(String propertyName, Object propertyValue) {
        ReflectUtil.setPropertyValue(propertyName, propertyValue, this.baseDomain);
    }

    public boolean isNull(String propertyName) {
        return ReflectUtil.isValueNull(propertyName, this.baseDomain);
    }

    public void copyValuesFromMap(Map<String, Object> values) {
        this.copyValuesFromMap(values, false, null);
    }

    public void copyValuesFromMap(Map<String, Object> values, PropertySetter setter) {
        this.copyValuesFromMap(values, false, setter);
    }

    public void copyValuesFromMap(Map<String, Object> values, boolean formatVariableWithLower) {
        this.copyValuesFromMap(values, formatVariableWithLower, null);
    }

    public void copyValuesFromMap(Map<String, Object> values, boolean formatVariableWithLower, PropertySetter setter) {
        if (formatVariableWithLower) {
            Map<String, Object> result = new HashMap<>();

            PropertySetter.Property property;
            for (Iterator<Map.Entry<String, Object>> entryIterator = values.entrySet().iterator(); entryIterator.hasNext(); result.put(TextUtil.formatVariableWithLower(property.getName()), property.getValue())) {
                Map.Entry<String, Object> entry = entryIterator.next();
                property = new PropertySetter.Property(entry);
                if (setter != null) {
                    setter.set(property);
                }
            }

            ReflectUtil.copyPropertiesFromMap(this.baseDomain, result);
        } else {
            ReflectUtil.copyPropertiesFromMap(this.baseDomain, values);
        }

    }

    public void convertValueCharset(String sourceCharset, String targetCharset) {
        try {
            for (TableColumnDef column : this.metadata.getColumns()) {
                Object value = this.getValue(column.getName());
                if (value instanceof String) {
                    this.setValue(column.getName(), new String((value + "").getBytes(sourceCharset), targetCharset));
                }
            }

        } catch (Exception var6) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(var6));
        }
    }

    public Object simpleClone() {
        return ReflectUtil.simpleClone(this.baseDomain, this.baseDomain.getClass());
    }
}
