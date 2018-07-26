package cn.cloudwalk.smartframework.common.util.http.bean;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author LIYANHUI
 */
public class HttpClientNode {

    private PoolingHttpClientConnectionManager connManager;
    private CloseableHttpClient client;

    public HttpClientNode() {
    }

    public HttpClientNode(PoolingHttpClientConnectionManager connManager, CloseableHttpClient client) {
        this.connManager = connManager;
        this.client = client;
    }

    public PoolingHttpClientConnectionManager getConnManager() {
        return this.connManager;
    }

    public void setConnManager(PoolingHttpClientConnectionManager connManager) {
        this.connManager = connManager;
    }

    public CloseableHttpClient getClient() {
        return this.client;
    }

    public void setClient(CloseableHttpClient client) {
        this.client = client;
    }
}
