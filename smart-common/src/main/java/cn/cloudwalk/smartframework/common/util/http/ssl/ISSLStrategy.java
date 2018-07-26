package cn.cloudwalk.smartframework.common.util.http.ssl;

import cn.cloudwalk.smartframework.common.IBaseComponent;

/**
 * @author LIYANHUI
 */
public interface ISSLStrategy extends IBaseComponent {
    String DEFAULT_CERT_NAME = "default";
    String SKIP = "_skip";

    String decide(String url, Object params);
}
