package cn.cloudwalk.smartframework.common.util.http.async;

import cn.cloudwalk.smartframework.common.util.http.bean.HttpRequest;
import org.apache.http.StatusLine;

import java.util.List;

/**
 * @author LIYANHUI
 */
public abstract class AsyncCallbackAdapter implements AsyncCallback {

    @Override
    public void onStart() {
    }

    @Override
    public void onProgressChange(long index, long total) {
    }

    @Override
    public void onCancel(HttpRequest metadata) {
    }

    @Override
    public void onComplete(String responseText, HttpRequest metadata, StatusLine status) {
    }

    @Override
    public void onEnd(List<HttpRequest> resultList) {
    }
}
