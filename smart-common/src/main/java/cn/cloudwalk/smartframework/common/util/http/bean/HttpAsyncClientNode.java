package cn.cloudwalk.smartframework.common.util.http.bean;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;

/**
 * @author LIYANHUI
 */
public class HttpAsyncClientNode {

    private PoolingNHttpClientConnectionManager connManager;
    private CloseableHttpAsyncClient client;

    public HttpAsyncClientNode() {
    }

    public HttpAsyncClientNode(PoolingNHttpClientConnectionManager connManager, CloseableHttpAsyncClient client) {
        this.connManager = connManager;
        this.client = client;
    }

    public PoolingNHttpClientConnectionManager getConnManager() {
        return this.connManager;
    }

    public void setConnManager(PoolingNHttpClientConnectionManager connManager) {
        this.connManager = connManager;
    }

    public CloseableHttpAsyncClient getClient() {
        return this.client;
    }

    public void setClient(CloseableHttpAsyncClient client) {
        this.client = client;
    }
}
