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

    private static final String DEFAULT_CERT_NAME = "default";
    private static final String SKIP = "_skip";
    private static final String CONFIG_NAME = "application.properties";
    private static Logger logger = LogManager.getLogger(SSLConfigUtil.class);
    private static Properties applicationCfg = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir(CONFIG_NAME);
    private static ConcurrentMap<String, SSLContext> context = new ConcurrentHashMap<>();
    private static ISSLStrategy strategy;

    static {
        if (isConfiguredHttps()) {
            logger.info("init system.https.sslStrategy policy");
            if (!applicationCfg.containsKey("system.https.sslStrategy")) {
                logger.info("application.properties not config system.https.sslStrategy，use default cn.cloudwalk.smartframework.common.util.http.ssl.DefaultSSLStrategy");
            }

            String strategyClass = applicationCfg.getProperty("system.https.sslStrategy", "cn.cloudwalk.smartframework.common.util.http.ssl.DefaultSSLStrategy");
            if (TextUtil.isEmpty(strategyClass)) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("error system.https.sslStrategy config：" + strategyClass));
            }

            try {
                Object obj = Class.forName(strategyClass).newInstance();
                if (!(obj instanceof ISSLStrategy)) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("error system.https.sslStrategy config，strategy must be the impl class of " + ISSLStrategy.class.getName() + "，but now is  " + strategyClass));
                }

                strategy = (ISSLStrategy) obj;
                logger.info("system.https.sslStrategy init completed，strategy is " + strategyClass);
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
        if (SKIP.equals(certName)) {
            return buildTrustSSLContext();
        } else if (isConfiguredHttps()) {
            logger.info("build SSLContext with（certName=" + certName + "）");
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
                logger.info("SSLContext build completed with（certName=" + certName + "）");
                return ctx;
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("CONFIG ERROR：application.properties not config https cert base path （system.https.certBasePath），SSLContext build failed"));
        }
    }

    public static String decideCertName(String url, Object params) {
        return strategy != null ? strategy.decide(url, params) : DEFAULT_CERT_NAME;
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
