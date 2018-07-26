package cn.cloudwalk.smartframework.core.dao.datasource;


/**
 * @author LIYANHUI
 */
public interface IDataSourceStrategy {

    void init();

    String beforeInvoke(StrategyCutpoint var1);

    String afterInvoke(StrategyCutpoint var1);

    enum OPERATE_TYPE {
        READ,
        WRITE;

        OPERATE_TYPE() {
        }
    }
}
