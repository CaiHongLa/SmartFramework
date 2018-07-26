package cn.cloudwalk.smartframework.common.util.http;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.FileUtil;
import cn.cloudwalk.smartframework.common.util.PropertiesUtil;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.common.util.http.ssl.ISSLStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author LIYANHUI
 */
public class SSLConfigUtil {

    public static final String DEFAULT_CERT_NAME = "default";
    public static final String SKIP = "_skip";
    private static final String CONFIG_NAME = "application-cfg.properties";
    private static Logger logger = LogManager.getLogger(SSLConfigUtil.class);
    private static Properties applicationCfg = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir(CONFIG_NAME);
    private static ConcurrentMap<String, SSLContext> context = new ConcurrentHashMap<>();
    private static ISSLStrategy strategy;

    static {
        if (isConfiguredHttps()) {
            String STRATEGY_KEY = "system.https.sslStrategy";
            String DEFAULT_STRATEGY_CLASS_NAME = "cn.cloudwalk.smartframework.common.util.http.ssl.DefaultSSLStrategy";
            logger.info("开始初始化 system.https.sslStrategy 策略");
            if (!applicationCfg.containsKey("system.https.sslStrategy")) {
                logger.info("application-cfg.properties 配置文件中没有找到 system.https.sslStrategy 策略配置项，因此将默认使用 cn.cloudwalk.smartframework.common.util.http.ssl.DefaultSSLStrategy");
            }

            String strategyClass = applicationCfg.getProperty("system.https.sslStrategy", "cn.cloudwalk.smartframework.common.util.http.ssl.DefaultSSLStrategy");
            if (!TextUtil.isNotEmpty(strategyClass)) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("无效的 system.https.sslStrategy 配置值：" + strategyClass));
            }

            try {
                Object obj = Class.forName(strategyClass).newInstance();
                if (!(obj instanceof ISSLStrategy)) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("无效的 system.https.sslStrategy 配置，策略类必须为 " + ISSLStrategy.class.getName() + " 的实现类，但配置的是 " + strategyClass));
                }

                strategy = (ISSLStrategy) obj;
                logger.info("初始化 system.https.sslStrategy 策略完成，策略类为 " + strategyClass);
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }

    }

    public SSLConfigUtil() {
    }

    public static boolean isConfiguredHttps() {
        return applicationCfg != null && applicationCfg.containsKey("system.https.certBasePath");
    }

    private static SSLContext buildTrustSSLContext() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            TrustStrategy anyTrustStrategy = (x509Certificates, s) -> true;
            return SSLContexts.custom().useProtocol("TLS").loadTrustMaterial(trustStore, anyTrustStrategy).build();
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    private static SSLContext buildSSLContext(String certName) {
        if ("_skip".equals(certName)) {
            return buildTrustSSLContext();
        } else if (isConfiguredHttps()) {
            logger.info("开始构建 SSLContext 对象（certName=" + certName + "）");
            String certPath = applicationCfg.getProperty("system.https.certBasePath") + File.separator + certName + File.separator;
            String keyStorePasswordText = FileUtil.loadResourceAsTextOnClassPathOrConfigDir(certPath + "person-client.pass");
            String trustKeyStorePasswordText = FileUtil.loadResourceAsTextOnClassPathOrConfigDir(certPath + "cloudwalk-truststore.pass");

            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                KeyStore trustStore = KeyStore.getInstance("JKS");

                try (InputStream keystream = FileUtil.loadResourceAsStreamOnClassPathOrConfigDir(certPath + "person-client.p12"); InputStream truststream = FileUtil.loadResourceAsStreamOnClassPathOrConfigDir(certPath + "cloudwalk-truststore.jks")) {
                    keyStore.load(keystream, keyStorePasswordText.toCharArray());
                    trustStore.load(truststream, trustKeyStorePasswordText.toCharArray());
                }

                SSLContext ctx = SSLContexts.custom().useProtocol("TLS").loadKeyMaterial(keyStore, keyStorePasswordText.toCharArray()).loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
                logger.info("SSLContext 对象构建完成（certName=" + certName + "）");
                return ctx;
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("配置错误：application-cfg.properties 配置文件中没有指定 https 证书基路径（system.https.certBasePath），因此 SSLContext 构建失败"));
        }
    }

    public static String decideCertName(String url, Object params) {
        return strategy != null ? strategy.decide(url, params) : "default";
    }

    public static SSLContext getSSLContext(String certName) {
        if (context.containsKey(certName)) {
            return context.get(certName);
        } else {
            SSLContext ctx = buildSSLContext(certName);
            context.put(certName, ctx);
            return ctx;
        }
    }
}
