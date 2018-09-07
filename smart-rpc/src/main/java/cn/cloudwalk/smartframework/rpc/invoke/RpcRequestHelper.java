package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.clientcomponents.client.CloseableClient;
import cn.cloudwalk.smartframework.clientcomponents.client.TcpRoute;
import cn.cloudwalk.smartframework.clientcomponents.client.conn.PoolingTcpClientConnectionManager;
import cn.cloudwalk.smartframework.clientcomponents.client.pool.CPool;
import cn.cloudwalk.smartframework.clientcomponents.core.config.RequestConfig;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.PropertiesUtil;
import cn.cloudwalk.smartframework.rpc.client.RpcClientBuilder;
import cn.cloudwalk.smartframework.rpc.client.RpcClientConnectionOperator;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Rpc请求辅助类，用于发送Rpc请求以及处理请求结果
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/8/15
 * @since 2.0.10
 */
public final class RpcRequestHelper {

    private static final Logger logger = LogManager.getLogger(RpcRequestHelper.class);

    private static ConcurrentMap<TcpRoute, CloseableClient> clients;
    private static Map<String, String> params;

    static {
        Properties config = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir("application.properties");
        if (config != null) {
            params = new HashMap<>(config.size());
            for (Object key : config.keySet()) {
                params.put((String) key, (String) config.get(key));
            }
        }
    }

    private RpcRequestHelper() {

    }

    /**
     * @param key
     * @param defaultValue
     * @return
     * @since 2.0.10
     */
    public static int getIntParam(String key, int defaultValue) {
        if (params != null && params.containsKey(key)) {
            return Integer.parseInt(params.get(key) + "");
        } else {
            logger.info("not found " + key + "，will use default value " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * @param route
     * @return
     * @since 2.0.10
     */
    static synchronized CloseableClient getOrCreateClient(TcpRoute route) {
        long start = System.currentTimeMillis();
        if (clients == null) {
            clients = new ConcurrentHashMap<>();
        }
        logger.info("hash : " + ( System.currentTimeMillis() - start));
        if (clients.containsKey(route)) {
            return clients.get(route);
        } else {
            logger.info("init CloseableClient");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setMaxPerRoute(getIntParam(ProtocolConstants.RPC_CLIENT_MAX_PER_ROUTE, 5000))
                    .setMaxTimeToLive(getIntParam(ProtocolConstants.RPC_CLIENT_MAX_TIME_LIVE, 60000))
                    .setMaxTotal(getIntParam(ProtocolConstants.RPC_CLIENT_MAX_TOTOL, 5000))
                    .setParams(params)
                    .build();
            PoolingTcpClientConnectionManager connManager = createConnectionManager(requestConfig);
            CloseableClient closeableClient = RpcClientBuilder.create()
                    .setConnectionManager(connManager)
                    .setDefaultRequestConfig(requestConfig)
                    .setMaxIdleTime(getIntParam(ProtocolConstants.RPC_CLIENT_MAX_IDLE_TIME, 60), TimeUnit.SECONDS)
                    .build();
            clients.put(route, closeableClient);
            logger.info("CloseableClient init completed");
            return closeableClient;
        }
    }

    /**
     * @since 2.0.10
     */
    public static void closeRpcClient() {
        try {
            if (clients != null && clients.size() > 0) {
                logger.info("closing clients");

                for (CloseableClient client : clients.values()) {
                    client.close();
                }

                logger.info("clients closed");
            }

        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    /**
     * @param requestConfig
     * @return
     * @since 2.0.10
     */
    private static PoolingTcpClientConnectionManager createConnectionManager(RequestConfig requestConfig) {
        logger.info("init PoolingTcpClientConnectionManager");
        PoolingTcpClientConnectionManager connManager = new PoolingTcpClientConnectionManager(
                new AtomicBoolean(false),
                new RpcClientConnectionOperator(),
                new CPool(new PoolingTcpClientConnectionManager.InternalConnectionFactory(),
                        requestConfig.getMaxPerRoute(),
                        requestConfig.getMaxTotal(),
                        requestConfig.getMaxTimeToLive(),
                        TimeUnit.MILLISECONDS),
                new PoolingTcpClientConnectionManager.ConfigData());
        logger.info("PoolingTcpClientConnectionManager init completed");
        return connManager;
    }

}
