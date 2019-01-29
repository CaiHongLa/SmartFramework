package cn.cloudwalk.smartframework.core.dao.datasource;

import cn.cloudwalk.smartframework.common.IConfigurationService;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 动态数据源管理
 * 支持将application.properties文件中配置的所有数据源加载到spring上下文中，可以在Dao层和Service层动态切换
 *
 * @author LIYANHUI
 * @see org.springframework.jdbc.datasource.AbstractDataSource
 */
public class DynamicDataSource extends AbstractRoutingDataSource implements ApplicationContextAware {

    /**
     * 默认的数据源名称 master
     */
    public static String DEFAULT_DATA_SOURCE_NAME;
    /**
     */
    private static final Logger logger = LogManager.getLogger(DynamicDataSource.class);
    /**
     * Spring文本
     */
    private ApplicationContext applicationContext;
    /**
     * 存储已经加载的数据源
     */
    private Map<Object, Object> loadedDataSources;

    @Autowired(required = false)
    private IConfigurationService configurationService;

    /**
     * 无参构造，初始化一个空的MAP对象
     */
    public DynamicDataSource() {
        this.loadedDataSources = new HashMap<>();
    }

    /**
     * 在WEB容器启动完成之后从配置文件中加载数据源
     */
    @PostConstruct
    public void init() {
        logger.info("registering data source");
        if(null == configurationService){
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("IConfigurationService is null，please import Config module in your application context！"));
        }
        Properties jdbcConfig = configurationService.getApplicationCfg();
        if (jdbcConfig != null && jdbcConfig.containsKey("ds.use")) {
            String use = jdbcConfig.getProperty("ds.use");
            String major = jdbcConfig.getProperty("ds.major");
            String[] dataSourceNames = use.split(",");

            for (String dataSourceName : dataSourceNames) {
                String prefix = "ds." + dataSourceName + ".";
                String type = jdbcConfig.getProperty(prefix + "type").toUpperCase();
                boolean isSupport = DataSourceUtil.DATA_SOURCE_TYPE.containsByName(type);
                if (!isSupport) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("not support data source type：" + type));
                }

                Map<String, Object> config = PropertiesUtil.filter(prefix, "type|charsetConverter", true, jdbcConfig);
                registerDataSource(dataSourceName, config, DataSourceUtil.DATA_SOURCE_TYPE.valueOf(type));
            }

            setDefaultDataSouce(major);
            setTargetDataSources(loadedDataSources);
            afterPropertiesSet();
            logger.info("data source registered");
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(" application.properties config error，not found ds.use property"));
        }
    }


    public void registerDataSource(String id, Map<String, Object> properties, DataSourceUtil.DATA_SOURCE_TYPE dataSourceType) {
        if (loadedDataSources.containsKey(id)) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("data source " + id + " exist"));
        } else {
            logger.info("registering data source （id=" + id + ", properties=" + properties + ", dataSourceType=" + dataSourceType + "）");
            DataSourceUtil.register(id, properties, dataSourceType, applicationContext);
            DataSource dataSource = applicationContext.getBean(id, DataSource.class);
            loadedDataSources.put(id, dataSource);
            logger.info("registered data source（id=" + id + ", properties=" + properties + ", dataSourceType=" + dataSourceType + "）");
            Set<String> loadedDs = this.getLoadedDataSourceNames();
            logger.info("loaded " + loadedDs.size() + " data source ：" + loadedDs);
        }
    }

    public String getDefaultDataSourceName() {
        return DEFAULT_DATA_SOURCE_NAME;
    }

    public Map<String, DataSource> getLoadedDataSource() {
        Map<String, DataSource> all = new HashMap<>();

        for (Map.Entry<Object, Object> ds : loadedDataSources.entrySet()) {
            all.put(ds.getKey() + "", (DataSource) ds.getValue());
        }

        return all;
    }

    public Set<String> getLoadedDataSourceNames() {
        return this.getLoadedDataSource().keySet();
    }

    public boolean contains(String name) {
        return loadedDataSources.containsKey(name);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getCurrentDataSourceName();
    }

    public void setDefaultDataSouce(String id) {
        DEFAULT_DATA_SOURCE_NAME = id;
        DataSource ds = applicationContext.getBean(id, DataSource.class);
        setDefaultTargetDataSource(ds);
        logger.info("set default data source：" + id);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
