package cn.cloudwalk.smartframework.common.util.http.bean;

/**
 * @author LIYANHUI
 */
public class ProtocolOutResponseMetadata {

    private String url;

    public ProtocolOutResponseMetadata() {
    }

    public ProtocolOutResponseMetadata(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ProtocolOutResponseMetadata [url=" + this.url + "]";
    }
}
