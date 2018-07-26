package cn.cloudwalk.smartframework.common.util.http.ssl;

/**
 * @author LIYANHUI
 */
public class DefaultSSLStrategy extends BaseSSLStrategy {

    @Override
    public String decide(String url, Object params) {
        return "default";
    }
}
