package cn.cloudwalk.smartframework.common.util.http;

import java.io.InputStream;

/**
 * @author LIYANHUI
 */
public interface HttpDownloadListener {
    void onComplete(InputStream var1) throws Exception;
}
