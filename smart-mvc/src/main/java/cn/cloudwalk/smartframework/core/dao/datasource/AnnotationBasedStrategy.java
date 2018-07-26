package cn.cloudwalk.smartframework.core.dao.datasource;

import java.lang.reflect.Method;

/**
 * @author LIYANHUI
 */
public class AnnotationBasedStrategy extends DataSourceStrategyAdapter {

    @Override
    public String beforeInvoke(StrategyCutpoint cutpoint) {
        Method method = cutpoint.getMethod();
        if (method != null && method.isAnnotationPresent(TargetDataSource.class)) {
            TargetDataSource ds = method.getAnnotation(TargetDataSource.class);
            return ds.value();
        } else {
            return DynamicDataSource.DEFAULT_DATA_SOURCE_NAME;
        }
    }

    @Override
    public String afterInvoke(StrategyCutpoint cutpoint) {
        return DynamicDataSource.DEFAULT_DATA_SOURCE_NAME;
    }
}
