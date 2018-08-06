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
        logger.info("开始初始化代理配置，配置信息为 " + applicationCfg);
        if (applicationCfg.containsKey("system.http.proxy.use")) {
            isUse = Boolean.valueOf(applicationCfg.getProperty("system.http.proxy.use"));
            if (!isUse) {
                logger.info("注意：application.properties 中配置了 system.http.proxy.use=false，因此 http proxy 功能被关闭，所有试图使用 http proxy 的功能都将会出错");
            } else {
                String ip = applicationCfg.getProperty("system.http.proxy.ip");
                Integer port = Integer.valueOf(applicationCfg.getProperty("system.http.proxy.port"));
                String user = applicationCfg.getProperty("system.http.proxy.user");
                String password = applicationCfg.getProperty("system.http.proxy.password");
                provider = new BasicCredentialsProvider();
                provider.setCredentials(new AuthScope(ip, port), new UsernamePasswordCredentials(user, password));
                config = RequestConfig.custom().setProxy(new HttpHost(ip, port)).setSocketTimeout(HttpBaseSupportUtil.getIntParam("soTimeout", 180000)).build();
            }

            logger.info("代理配置初始化完成");
        } else {
            logger.warn("注意：application.properties 配置文件中没有配置 proxy，因此所有试图使用 http proxy 的功能都将会出错");
        }

        logger.info("代理配置初始化完成");
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
