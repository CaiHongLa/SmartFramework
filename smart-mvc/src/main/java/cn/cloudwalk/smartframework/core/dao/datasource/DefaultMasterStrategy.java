package cn.cloudwalk.smartframework.core.dao.datasource;

/**
 * @author LIYANHUI
 */
public class DefaultMasterStrategy extends DataSourceStrategyAdapter {

    @Override
    public String beforeInvoke(StrategyCutpoint cutpoint) {
        return DynamicDataSource.DEFAULT_DATA_SOURCE_NAME;
    }

    @Override
    public String afterInvoke(StrategyCutpoint cutpoint) {
        return DynamicDataSource.DEFAULT_DATA_SOURCE_NAME;
    }
}
