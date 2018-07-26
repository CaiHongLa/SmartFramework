package cn.cloudwalk.smartframework.config.config;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.IConfigurationService;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.FileUtil;
import cn.cloudwalk.smartframework.common.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @author LIYANHUI
 */
@Component("configurationService")
public class ConfigurationService extends BaseComponent implements IConfigurationService {

    private static final String APPLICATION_CFG_FILE_NAME = "application-cfg.properties";
    private static final Logger logger = LogManager.getLogger(ConfigurationService.class);
    private Properties applicationCfg;

    @PostConstruct
    private void initApplicationConfig() {
        if (!FileUtil.isFileExistOnClasspathOrConfigDir(APPLICATION_CFG_FILE_NAME)) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("在类路径下没有找到配置文件 " + APPLICATION_CFG_FILE_NAME));
        } else {
            logger.info("开始加载配置文件 " + APPLICATION_CFG_FILE_NAME);
            this.applicationCfg = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir(APPLICATION_CFG_FILE_NAME);
            logger.info("配置文件 " + APPLICATION_CFG_FILE_NAME + " 加载完成：" + this.applicationCfg);
        }
    }

    @Override
    public Properties getApplicationCfg() {
        return this.applicationCfg;
    }

}