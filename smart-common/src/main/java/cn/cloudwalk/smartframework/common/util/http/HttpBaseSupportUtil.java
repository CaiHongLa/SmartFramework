package cn.cloudwalk.smartframework.common.util.http;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.model.IDataModel;
import cn.cloudwalk.smartframework.common.util.JsonUtil;
import cn.cloudwalk.smartframework.common.util.PropertiesUtil;
import cn.cloudwalk.smartframework.common.util.ReflectUtil;
import cn.cloudwalk.smartframework.common.util.http.bean.HTTP_CONTENT_TRANSFER_TYPE;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpAsyncClientNode;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpClientNode;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpRequest;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author LIYANHUI
 */
public class HttpBaseSupportUtil {

    private static Logger logger = LogManager.getLogger(HttpBaseSupportUtil.class);
    private static ConcurrentMap<Integer, HttpAsyncClientNode> asyncClients;
    private static ConcurrentMap<Integer, HttpClientNode> clients;
    private static Map<String, Object> params;

    static {
        Properties config = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir("application.properties");
        if (config != null) {
            logger.info("加载到配置文件 application.properties，其内容为 " + config);
            params = PropertiesUtil.filter("system.http.params.", true, config);
            logger.info("过滤后的配置参数为 " + params);
        } else {
            logger.info("没有加载到配置文件 application.properties，将使用默认参数进行配置");
        }

    }

    private HttpBaseSupportUtil() {
    }

    private static PoolingNHttpClientConnectionManager createAsyncConnectionManager(String certName) {
        logger.info("开始初始化 PoolingNHttpClientConnectionManager");
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(getIntParam("ioThreadCount", Runtime.getRuntime().availableProcessors() * 40)).setConnectTimeout(getIntParam("connectTimeout", 30000)).setSoTimeout(getIntParam("soTimeout", 180000)).setTcpNoDelay(true).setBacklogSize(getIntParam("backlogSize", 512)).build();

        try {
            ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
            RegistryBuilder<SchemeIOSessionStrategy> builder = RegistryBuilder.<SchemeIOSessionStrategy>create().register("http", new NoopIOSessionStrategy());
            if (SSLConfigUtil.isConfiguredHttps()) {
                builder.register("https", new SSLIOSessionStrategy(SSLConfigUtil.getSSLContext(certName), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
            }

            Registry<SchemeIOSessionStrategy> sslioSessionRegistry = builder.build();
            PoolingNHttpClientConnectionManager asyncConnManager = new PoolingNHttpClientConnectionManager(ioReactor, sslioSessionRegistry);
            asyncConnManager.setMaxTotal(getIntParam("maxTotal", 5000));
            asyncConnManager.setDefaultMaxPerRoute(getIntParam("defaultMaxPerRoute", 1000));
            logger.info("初始化 PoolingNHttpClientConnectionManager 完成");
            return asyncConnManager;
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    private static PoolingHttpClientConnectionManager createConnectionManager(String certName) {
        logger.info("开始初始化 PoolingHttpClientConnectionManager");
        RegistryBuilder<ConnectionSocketFactory> builder = RegistryBuilder.<ConnectionSocketFactory>create().register("http", new PlainConnectionSocketFactory());
        if (SSLConfigUtil.isConfiguredHttps()) {
            builder.register("https", new SSLConnectionSocketFactory(SSLConfigUtil.getSSLContext(certName), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = builder.build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connManager.setMaxTotal(getIntParam("maxTotal", 5000));
        connManager.setDefaultMaxPerRoute(getIntParam("defaultMaxPerRoute", 1000));
        logger.info("初始化 PoolingHttpClientConnectionManager 完成");
        return connManager;
    }

    private static Integer generateHttpAsyncClientKey(boolean isHttps, boolean proxy, String certName) {
        return ("ASYNC_" + String.valueOf(isHttps) + proxy + certName).hashCode();
    }

    public static synchronized CloseableHttpAsyncClient getOrCreateAsyncHttpClient(String url, Object params, boolean proxy) {
        boolean isHttps = url.toLowerCase().startsWith("https://");
        String certName = SSLConfigUtil.decideCertName(url, params);
        Integer cacheKey = generateHttpAsyncClientKey(isHttps, proxy, certName);
        if (asyncClients == null) {
            asyncClients = new ConcurrentHashMap<>();
        }

        if (asyncClients.containsKey(cacheKey)) {
            HttpAsyncClientNode hcnode = asyncClients.get(cacheKey);
            if (hcnode.getClient().isRunning()) {
                return hcnode.getClient();
            }

            logger.error("发现 CloseableHttpAsyncClient 被异常关闭，准备重新初始化");
            HttpAsyncClientNode client = asyncClients.remove(cacheKey);

            try {
                client.getConnManager().shutdown();
            } catch (IOException ignored) {
            }
        }

        logger.info("开始初始化 CloseableHttpAsyncClient");
        ConnectionConfig connConfig = ConnectionConfig.custom().setBufferSize(131072).build();
        PoolingNHttpClientConnectionManager connManager = createAsyncConnectionManager(certName);
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom().disableCookieManagement().setConnectionManager(connManager).setDefaultConnectionConfig(connConfig);
        if (isHttps) {
            httpClientBuilder.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).setSSLContext(SSLConfigUtil.getSSLContext(certName));
        }

        if (proxy && ProxyConfigUtil.isUse()) {
            httpClientBuilder.setDefaultCredentialsProvider(ProxyConfigUtil.getCredentialsProvider());
        }

        CloseableHttpAsyncClient asyncHttpClient = httpClientBuilder.build();
        asyncHttpClient.start();
        asyncClients.put(cacheKey, new HttpAsyncClientNode(connManager, asyncHttpClient));
        logger.info("CloseableHttpAsyncClient 初始化完成");
        return asyncHttpClient;
    }

    public static synchronized CloseableHttpClient getOrCreateHttpClient(String url, Object params, boolean proxy) {
        boolean isHttps = url.toLowerCase().startsWith("https");
        String certName = SSLConfigUtil.decideCertName(url, params);
        Integer cacheKey = ("SYNC_" + String.valueOf(isHttps) + proxy + certName).hashCode();
        if (clients == null) {
            clients = new ConcurrentHashMap<>();
        }

        if (clients.containsKey(cacheKey)) {
            return clients.get(cacheKey).getClient();
        } else {
            logger.info("开始初始化 CloseableHttpClient");
            ConnectionConfig connConfig = ConnectionConfig.custom().setBufferSize(131072).build();
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(getIntParam("soTimeout", 180000)).build();
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(getIntParam("connectTimeout", 30000)).setSocketTimeout(getIntParam("soTimeout", 180000)).build();
            PoolingHttpClientConnectionManager connManager = createConnectionManager(certName);
            HttpClientBuilder builder = HttpClientBuilder.create().disableCookieManagement().setConnectionTimeToLive(30L, TimeUnit.SECONDS).evictIdleConnections(30L, TimeUnit.SECONDS).evictExpiredConnections().setDefaultConnectionConfig(connConfig).setDefaultRequestConfig(requestConfig).setDefaultSocketConfig(socketConfig).setConnectionManager(connManager);
            if (isHttps) {
                builder.setSSLContext(SSLConfigUtil.getSSLContext(certName)).setSSLHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            }

            if (proxy && ProxyConfigUtil.isUse()) {
                builder.setDefaultCredentialsProvider(ProxyConfigUtil.getCredentialsProvider());
            }

            CloseableHttpClient httpClient = builder.build();
            clients.put(cacheKey, new HttpClientNode(connManager, httpClient));
            logger.info("初始化 CloseableHttpClient 完成");
            return httpClient;
        }
    }

    public static HttpGet buildHttpGet(String url, Object params, boolean proxy) {
        return buildHttpGet(url, params, null, proxy, null);
    }

    public static HttpGet buildHttpGet(String url, Object params, boolean proxy, RequestConfig requestConfig) {
        return buildHttpGet(url, params, null, proxy, requestConfig);
    }

    public static HttpGet buildHttpGet(String url, Object params, List<? extends Header> headers, boolean proxy, RequestConfig requestConfig) {
        if (params != null && !(params instanceof Map)) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("参数类型错误，对于 get 请求，参数类型必须为 map，但现在是 " + params.getClass().getName()));
        } else {
            StringBuilder fullUrl = new StringBuilder(url);
            if (params != null) {
                Map paramsMap = (Map) params;
                if (paramsMap.size() > 0) {
                    Map.Entry param;
                    try {
                        for (Iterator var7 = paramsMap.entrySet().iterator(); var7.hasNext(); fullUrl.append((String) param.getKey()).append("=").append(UriUtils.encode(param.getValue() + "", "UTF-8"))) {
                            param = (Map.Entry) var7.next();
                            if (fullUrl.indexOf("?") == -1) {
                                fullUrl.append("?");
                            } else {
                                fullUrl.append("&");
                            }
                        }
                    } catch (Exception e) {
                        throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
                    }
                }
            }

            HttpGet httpGet = new HttpGet(fullUrl.toString());
            if (headers != null && headers.size() > 0) {
                httpGet.setHeaders(headers.toArray(new Header[0]));
            }

            if (requestConfig != null) {
                httpGet.setConfig(requestConfig);
            }

            if (proxy && ProxyConfigUtil.isUse()) {
                RequestConfig proxyCfg = ProxyConfigUtil.getRequestConfig();
                RequestConfig defaultCfg = httpGet.getConfig();
                if (defaultCfg == null) {
                    httpGet.setConfig(proxyCfg);
                } else {
                    httpGet.setConfig(RequestConfig.copy(defaultCfg).setProxy(proxyCfg.getProxy()).build());
                }
            }

            return httpGet;
        }
    }

    public static HttpPost buildHttpPost(String url, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean proxy) {
        return buildHttpPost(url, params, null, transferType, proxy, null);
    }

    public static HttpPost buildHttpPost(String url, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean proxy, RequestConfig requestConfig) {
        return buildHttpPost(url, params, null, transferType, proxy, requestConfig);
    }

    public static HttpPost buildHttpPost(String url, Object params, List<? extends Header> headers, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean proxy, RequestConfig requestConfig) {
        HttpPost httpPost = new HttpPost(url);
        if (headers != null && headers.size() > 0) {
            httpPost.setHeaders(headers.toArray(new Header[0]));
        }

        if (requestConfig != null) {
            httpPost.setConfig(requestConfig);
        }

        if (proxy && ProxyConfigUtil.isUse()) {
            RequestConfig proxyConfig = ProxyConfigUtil.getRequestConfig();
            RequestConfig defaultCfg = httpPost.getConfig();
            if (defaultCfg == null) {
                httpPost.setConfig(proxyConfig);
            } else {
                httpPost.setConfig(RequestConfig.copy(defaultCfg).setProxy(proxyConfig.getProxy()).build());
            }
        }

        if (params == null) {
            return httpPost;
        } else if (transferType == null) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("无效的 post 请求（" + url + "），当存在 params 时，则必须指定 transferType"));
        } else {
            if (transferType == HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE) {
                if (!(params instanceof Map) && !(params instanceof IDataModel)) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("请求（" + url + "）参数类型错误，对于 post 请求的 KEY_VALUE 形式，params 的类型仅能为 Map, IDataModel 中的其中一种，但现在是：" + params.getClass().getName()));
                }

                Map<String, Object> paramsMap;
                if (params instanceof Map) {
                    paramsMap = (Map<String, Object>) params;
                } else {
                    paramsMap = ReflectUtil.bean2Map(params);
                }

                List<NameValuePair> nvps = new ArrayList<>();

                for (Map.Entry<String, Object> param : paramsMap.entrySet()) {
                    Object value = param.getValue();
                    if (value != null) {
                        nvps.add(new BasicNameValuePair(param.getKey(), param.getValue() + ""));
                    }
                }

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
                }
            } else if (transferType == HTTP_CONTENT_TRANSFER_TYPE.JSON) {
                StringEntity body = new StringEntity(JsonUtil.object2Json(params), "UTF-8");
                body.setContentType(ContentType.APPLICATION_JSON.toString());
                httpPost.setEntity(body);
            }

            return httpPost;
        }
    }

    public static String getResponseText(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        StringBuilder content = new StringBuilder();
        InputStreamReader reader = null;

        try {
            reader = new InputStreamReader(entity.getContent(), "UTF-8");
            char[] tmp = new char[512];

            int len;
            while ((len = reader.read(tmp)) != -1) {
                content.append(new String(tmp, 0, len));
            }
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("关闭流异常！" + e);
                }
            }

        }

        return content.toString();
    }

    public static void checkRequestCodeConfig(List<HttpRequest> requestList) {
        if (requestList.size() != 1) {
            Set<String> codes = new HashSet<>();
            Iterator iterator = requestList.iterator();

            String code;
            String url;
            do {
                if (!iterator.hasNext()) {
                    return;
                }

                HttpRequest request = (HttpRequest) iterator.next();
                code = request.getCode();
                url = request.getUrl();
                if (code == null) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("批量异步请求前置检查失败：对于多个并发的异步请求，则必须为每个请求指定唯一标识符 code，但发现存在 code 为 null 的请求（url=" + url + "），因此所有请求拒绝执行"));
                }
            } while (codes.add(code));

            throw new FrameworkInternalSystemException(new SystemExceptionDesc("批量异步请求前置检查失败：对于多个并发的异步请求，则必须为每个请求指定唯一标识符 code，但发现存在重复的 code 的请求（url=" + url + "），因此所有请求拒绝执行"));
        }
    }

    public static int getIntParam(String key, int defaultValue) {
        if (params != null && params.containsKey(key)) {
            return Integer.parseInt(params.get(key) + "");
        } else {
            logger.info("没有找到或没有配置该项 " + key + "，将使用其默认值 " + defaultValue);
            return defaultValue;
        }
    }

    public static void closeAsyncHttpClient() {
        try {
            if (asyncClients != null && asyncClients.size() > 0) {
                logger.info("开始关闭 asyncClients");

                for (HttpAsyncClientNode client : asyncClients.values()) {
                    client.getConnManager().shutdown();
                    if (client.getClient().isRunning()) {
                        client.getClient().close();
                    }
                }

                logger.info("asyncClients 关闭完成");
            }

        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public static void closeHttpClient() {
        try {
            if (clients != null && clients.size() > 0) {
                logger.info("开始关闭 clients");

                for (HttpClientNode client : clients.values()) {
                    client.getConnManager().shutdown();
                    client.getClient().close();
                }

                logger.info("clients 关闭完成");
            }

        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }
}
