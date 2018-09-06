package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.clientcomponents.client.CloseableClient;
import cn.cloudwalk.smartframework.clientcomponents.client.IdleConnectionEvictor;
import cn.cloudwalk.smartframework.clientcomponents.client.InternalClient;
import cn.cloudwalk.smartframework.clientcomponents.client.conn.PoolingTcpClientConnectionManager;
import cn.cloudwalk.smartframework.clientcomponents.core.ClientConnectionManager;
import cn.cloudwalk.smartframework.clientcomponents.core.config.RequestConfig;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Rpc客户端Builder
 *
 * @author liyanhui
 * @since 2.0.10
 */
public class RpcClientBuilder {
    private PoolingTcpClientConnectionManager connManager;
    private RequestConfig defaultRequestConfig;
    private long maxIdleTime;
    private TimeUnit maxIdleTimeUnit;

    private List<Closeable> closeables = new ArrayList<>();

    public static RpcClientBuilder create() {
        return new RpcClientBuilder();
    }

    protected RpcClientBuilder() {
        super();
    }

    public RpcClientBuilder setDefaultRequestConfig(RequestConfig defaultRequestConfig) {
        this.defaultRequestConfig = defaultRequestConfig;
        return this;
    }

    public final RpcClientBuilder setConnectionManager(
            final PoolingTcpClientConnectionManager connManager) {
        this.connManager = connManager;
        return this;
    }

    public final RpcClientBuilder setMaxIdleTime(long maxIdleTime, TimeUnit timeUnit) {
        this.maxIdleTime = maxIdleTime;
        this.maxIdleTimeUnit = timeUnit;
        return this;
    }

    protected void addCloseable(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        if (closeables == null) {
            closeables = new ArrayList<Closeable>();
        }
        closeables.add(closeable);
    }


    public CloseableClient build() {

        PoolingTcpClientConnectionManager connManagerCopy = this.connManager;
        if (connManagerCopy == null) {
            throw new IllegalArgumentException("connManager may not be null");
        }
        if (defaultRequestConfig != null) {
            connManagerCopy.setDefaultRequestConfig(defaultRequestConfig);
        }

        List<Closeable> closeablesCopy = closeables;
        final IdleConnectionEvictor connectionEvictor = new IdleConnectionEvictor(connManagerCopy,
                maxIdleTime > 0 ? maxIdleTime : 60, maxIdleTimeUnit != null ? maxIdleTimeUnit : TimeUnit.SECONDS);
        closeablesCopy.add(connectionEvictor::shutdown);

        closeablesCopy.add(((ClientConnectionManager) connManagerCopy)::shutdown);
        connectionEvictor.start();
        return new InternalClient(connManagerCopy, closeables);
    }

}
