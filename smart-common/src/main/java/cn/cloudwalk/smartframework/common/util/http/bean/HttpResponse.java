package cn.cloudwalk.smartframework.common.util.http.bean;

import cn.cloudwalk.smartframework.common.util.JsonUtil;
import org.apache.http.StatusLine;

import java.util.Map;

/**
 * @author LIYANHUI
 */
public class HttpResponse {

    private String responseText;
    private StatusLine statusLine;
    private HttpRequest request;

    public HttpResponse() {
    }

    public HttpResponse(String responseText, StatusLine statusLine, HttpRequest request) {
        this.responseText = responseText;
        this.statusLine = statusLine;
        this.request = request;
    }

    public String getResponseText() {
        return this.responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public HttpRequest getRequest() {
        return this.request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public Map<String, Object> getAsMap() {
        return JsonUtil.json2Map(this.responseText);
    }

    @Override
    public String toString() {
        return "HttpResponse [statusLine=" + this.statusLine + ", request=" + this.request + "]";
    }
}
