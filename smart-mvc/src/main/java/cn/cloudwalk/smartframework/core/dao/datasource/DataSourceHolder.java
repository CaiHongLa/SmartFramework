package cn.cloudwalk.smartframework.core.dao.datasource;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 动态数据源切换
 *
 * @author LIYANHUI
 */
@Component("dataSourceHolder")
public class DataSourceHolder extends BaseComponent {
    private static ThreadLocal<String> currDataSource = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(DataSourceHolder.class);
    @Autowired(
            required = false
    )
    @Qualifier("dataSource")
    private DynamicDataSource dataSource;

    private IDataSourceStrategy dataSourceStrategy;

    public static void change(String key) {
        currDataSource.set(key);
    }

    public static void restore() {
        currDataSource.set(DynamicDataSource.DEFAULT_DATA_SOURCE_NAME);
    }

    public static String getCurrentDataSourceName() {
        return currDataSource.get();
    }

    @PostConstruct
    public void checkStrategy() {
        Properties jdbc = getConfigurationService().getApplicationCfg();
        if (!jdbc.containsKey("ds.strategy")) {
            dataSourceStrategy = new DefaultMasterStrategy();
            logger.warn("not config ds.strategy，use " + this.dataSourceStrategy.getClass().getName());
        } else {
            try {
                String className = jdbc.getProperty("ds.strategy").trim();
                Object obj = Class.forName(className).newInstance();
                if (!(obj instanceof IDataSourceStrategy)) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("ds.strategy （" + className + "） error，not impl of IDataSourceStrategy or not extends from DataSourceStrategyAdapter"));
                }

                dataSourceStrategy = (IDataSourceStrategy) obj;
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }

        dataSourceStrategy.init();
    }

    public void regist(String id, Map<String, Object> properties, DataSourceUtil.DATA_SOURCE_TYPE dataSourceType) {
        dataSource.registerDataSource(id, properties, dataSourceType);
    }

    public Map<String, DataSource> getLoadedDataSource() {
        return this.dataSource.getLoadedDataSource();
    }

    public Set<String> getLoadedDataSourceNames() {
        return this.dataSource.getLoadedDataSourceNames();
    }

    public boolean contains(String name) {
        return this.dataSource.contains(name);
    }

    public DynamicDataSource getDynamicDataSource() {
        return this.dataSource;
    }

    public void setDefaultDataSouce(String id) {
        this.dataSource.setDefaultDataSouce(id);
    }

    protected void beforeInvoke(JoinPoint point) {
        Object target = point.getTarget();
        String methodName = point.getSignature().getName();
        Class[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();

        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            String name = this.dataSourceStrategy.beforeInvoke(new StrategyCutpoint(methodName, method, point.getArgs(), target.getClass().getName(), target, this));
            change(name);
        } catch (Exception exception) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
        }
    }

    protected void afterInvoke(JoinPoint point) {
        Object target = point.getTarget();
        String methodName = point.getSignature().getName();
        Class[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();

        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            String name = this.dataSourceStrategy.afterInvoke(new StrategyCutpoint(methodName, method, point.getArgs(), target.getClass().getName(), target, this));
            change(name);
        } catch (Exception exception) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
        }
    }

    public IDataSourceStrategy getDataSourceStrategy() {
        return this.dataSourceStrategy;
    }

    public void setDataSourceStrategy(IDataSourceStrategy dataSouceStrategy) {
        this.dataSourceStrategy = dataSouceStrategy;
    }

    @PreDestroy
    public void destroy() {
        try {
            Set<String> dsNames = getLoadedDataSourceNames();
            if (dsNames != null) {
                logger.info("close data source （count: " + dsNames.size() + " ）");
                Map<String, Object> failure = new HashMap<>();

                for (String dsName : dsNames) {
                    Object datasource = getApplicationContext().getBean(dsName);
                    if (datasource instanceof ComboPooledDataSource) {
                        ComboPooledDataSource cpds = (ComboPooledDataSource) datasource;
                        cpds.close();

                        try {
                            DriverManager.deregisterDriver(DriverManager.getDriver(cpds.getJdbcUrl()));
                        } catch (SQLException e) {
                            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
                        }
                    } else {
                        failure.put(dsName, datasource.getClass().getName());
                        logger.warn("not support close data source （name=" + dsName + "，class=" + datasource.getClass().getName() + "）");
                    }
                }

                if (failure.size() != 0) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc(dsNames.size() - failure.size() + " data sources close failed ：" + failure));
                }

                logger.info("data source closed（count: " + dsNames.size() + " ）");
            }
        } finally {
            currDataSource.remove();
        }
    }
}
