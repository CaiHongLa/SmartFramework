package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.security.KeyStore;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Netty SSl连接工具类
 *
 * @author LIYANHUI
 * @date 2018/1/31
 * @since 1.0.0
 */
public class NettySslConfigUtil {

    private static final String CONFIG_NAME = "netty-cfg.properties";
    private static final String CERT_PATH = "netty.ssl.cert";
    private static Logger logger = LogManager.getLogger(NettySslConfigUtil.class);
    private static Properties applicationCfg = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir(CONFIG_NAME);
    private static ConcurrentMap<String, SSLContext> context = new ConcurrentHashMap<>();

    private static boolean isConfiguredSsl() {
        return applicationCfg != null && applicationCfg.containsKey(CERT_PATH);
    }

    private static final String PROTOCOL = "TLS";
    private static final String ALGORITHM = "SunX509";
    private static SSLContext clientContext;


    public static SSLContext buildServerSslContext(String certName) {
        if (isConfiguredSsl()) {
            String certPath = applicationCfg.getProperty(CERT_PATH) + File.separator + certName + File.separator;
            String keyStorePasswordText = FileUtil.loadResourceAsTextOnClassPathOrConfigDir(certPath + "ssl-server-keys.pass");
            String trustKeyStorePasswordText = FileUtil.loadResourceAsTextOnClassPathOrConfigDir(certPath + "ssl-server-trust.pass");
            try {
                KeyStore ks = KeyStore.getInstance("JKS");
                ks.load(FileUtil.loadResourceAsStreamOnClassPathOrConfigDir(certPath + "ssl-server-keys"), keyStorePasswordText.toCharArray());
                KeyStore tks = KeyStore.getInstance("JKS");
                tks.load(FileUtil.loadResourceAsStreamOnClassPathOrConfigDir(certPath + "ssl-server-trust"), trustKeyStorePasswordText.toCharArray());
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(ALGORITHM);
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(ALGORITHM);
                kmf.init(ks, keyStorePasswordText.toCharArray());
                tmf.init(tks);
                SSLContext serverContext = SSLContext.getInstance(PROTOCOL);
                serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                logger.info("Ssl Server Context 对象构建完成（certName=" + certName + "）");
                return serverContext;
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        } else {
            return null;
        }
    }

    public static void buildClientSslContext(String certName) {
        if (isConfiguredSsl()) {
            String certPath = applicationCfg.getProperty(CERT_PATH) + File.separator + certName + File.separator;
            String keyStorePasswordText = FileUtil.loadResourceAsTextOnClassPathOrConfigDir(certPath + "ssl-client-keys.pass");
            String trustKeyStorePasswordText = FileUtil.loadResourceAsTextOnClassPathOrConfigDir(certPath + "ssl-client-trust.pass");
            try {
                KeyStore ks2 = KeyStore.getInstance("JKS");
                ks2.load(FileUtil.loadResourceAsStreamOnClassPathOrConfigDir(certPath + "ssl-client-keys"), keyStorePasswordText.toCharArray());
                KeyStore tks2 = KeyStore.getInstance("JKS");
                tks2.load(FileUtil.loadResourceAsStreamOnClassPathOrConfigDir(certPath + "ssl-client-trust"), trustKeyStorePasswordText.toCharArray());
                KeyManagerFactory kmf2 = KeyManagerFactory.getInstance(ALGORITHM);
                TrustManagerFactory tmf2 = TrustManagerFactory.getInstance(ALGORITHM);
                kmf2.init(ks2, keyStorePasswordText.toCharArray());
                tmf2.init(tks2);
                clientContext = SSLContext.getInstance(PROTOCOL);
                clientContext.init(kmf2.getKeyManagers(), tmf2.getTrustManagers(), null);
                logger.info("Ssl Client Context 对象构建完成（certName=" + certName + "）");
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }
    }

    private static SSLContext getServerSSLContext(String certName) {
        if (context.containsKey(certName)) {
            return context.get(certName);
        } else {
            SSLContext ctx = buildServerSslContext(certName);
            if (ctx != null) {
                context.put(certName, ctx);
            }
            return ctx;
        }
    }

    public static void addSslHandler(ChannelPipeline channelPipeline, String certName) {
        SSLContext sslContext = getServerSSLContext(certName);
        if (null != sslContext) {
            SSLEngine sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"});
            channelPipeline.addFirst("ssl", new SslHandler(sslEngine));
        }
    }

    /**
     * 只有NettyClient使用，无意义
     *
     * @param pipeline
     */
    public static void addNettyClientSslHandler(ChannelPipeline pipeline) {
        if (clientContext == null) {
            buildClientSslContext("tcp");
        }
        if (null != clientContext) {
            SSLEngine sslEngine = clientContext.createSSLEngine();
            sslEngine.setUseClientMode(true);
            pipeline.addFirst("ssl", new SslHandler(sslEngine));
        }
    }

}