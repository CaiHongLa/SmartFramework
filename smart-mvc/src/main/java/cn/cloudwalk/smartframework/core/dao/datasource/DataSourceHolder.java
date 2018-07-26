package cn.cloudwalk.smartframework.core.dao.datasource;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.FileUtil;
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
        String CONFIG_FILE_NAME = "data-source.properties";
        if (!FileUtil.isClassPathFileExist(CONFIG_FILE_NAME)) {
            logger.info("请注意：没有找到配置文件 " + CONFIG_FILE_NAME + "，可能该项目无需数据库存储，因此一切调用数据库的方法都将会出错");
        } else {
            Properties jdbc = FileUtil.loadClassPathProperties(CONFIG_FILE_NAME);
            if (!jdbc.containsKey("ds.strategy")) {
                dataSourceStrategy = new DefaultMasterStrategy();
                logger.warn("没有指定 ds.strategy，已自动启用 " + this.dataSourceStrategy.getClass().getName());
            } else {
                try {
                    String className = jdbc.getProperty("ds.strategy").trim();
                    Object obj = Class.forName(className).newInstance();
                    if (obj == null || !(obj instanceof IDataSourceStrategy)) {
                        throw new FrameworkInternalSystemException(new SystemExceptionDesc("类名（" + className + "）错误，或没有实现自 IDataSourceStrategy，或没有继承自 DataSourceStrategyAdapter"));
                    }

                    dataSourceStrategy = (IDataSourceStrategy) obj;
                } catch (Exception e) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
                }
            }

            dataSourceStrategy.init();
        }
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
                logger.info("开始关闭数据源（待关闭 " + dsNames.size() + " 个）");
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
                        logger.warn("不支持关闭此类型的数据源（name=" + dsName + "，class=" + datasource.getClass().getName() + "）");
                    }
                }

                if (failure.size() != 0) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc(dsNames.size() - failure.size() + " 个数据源关闭失败：" + failure));
                }

                logger.info("所有数据源均关闭完成（共计 " + dsNames.size() + " 个）");
            }
        }finally {
            currDataSource.remove();
        }
    }
}
