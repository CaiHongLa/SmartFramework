package cn.cloudwalk.smartframework.common.util.http.async;

import cn.cloudwalk.smartframework.common.util.http.bean.HttpRequest;
import org.apache.http.StatusLine;

import java.util.List;

/**
 * @author LIYANHUI
 */
public interface AsyncCallback {
    void onStart();

    void onComplete(String responseText, HttpRequest metadata, StatusLine status);

    void onProgressChange(long index, long total);

    void onError(Exception e, HttpRequest metadata);

    void onCancel(HttpRequest metadata);

    void onEnd(List<HttpRequest> resultList);
}
