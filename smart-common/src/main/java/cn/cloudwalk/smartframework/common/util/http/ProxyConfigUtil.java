package cn.cloudwalk.smartframework.common.util.http;

import cn.cloudwalk.smartframework.common.util.PropertiesUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * @author LIYANHUI
 */
public class ProxyConfigUtil {

    private static final String CONFIG_NAME = "application.properties";
    private static Logger logger = LogManager.getLogger(ProxyConfigUtil.class);
    private static boolean isUse;
    private static CredentialsProvider provider;
    private static RequestConfig config;

    static {
        Properties applicationCfg = PropertiesUtil.loadClassPathProperties(CONFIG_NAME);
        logger.info("init proxy config, " + applicationCfg);
        if (applicationCfg.containsKey("system.http.proxy.use")) {
            isUse = Boolean.valueOf(applicationCfg.getProperty("system.http.proxy.use"));
            if (!isUse) {
                logger.info("WARN：application.properties config system.http.proxy.use=false，so http proxy will not be used， http proxy function will be error" );
            } else {
                String ip = applicationCfg.getProperty("system.http.proxy.ip");
                Integer port = Integer.valueOf(applicationCfg.getProperty("system.http.proxy.port"));
                String user = applicationCfg.getProperty("system.http.proxy.user");
                String password = applicationCfg.getProperty("system.http.proxy.password");
                provider = new BasicCredentialsProvider();
                provider.setCredentials(new AuthScope(ip, port), new UsernamePasswordCredentials(user, password));
                config = RequestConfig.custom().setProxy(new HttpHost(ip, port)).setSocketTimeout(HttpBaseSupportUtil.getIntParam("soTimeout", 180000)).build();
            }

            logger.info("proxy init completed");
        } else {
            logger.warn("WARN：application.properties not config proxy， http proxy function will be error");
        }
    }

    public ProxyConfigUtil() {
    }

    public static boolean isUse() {
        return isUse;
    }

    public static CredentialsProvider getCredentialsProvider() {
        return provider;
    }

    public static RequestConfig getRequestConfig() {
        return config;
    }
}
