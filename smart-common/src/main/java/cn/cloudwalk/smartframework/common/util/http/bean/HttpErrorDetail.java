package cn.cloudwalk.smartframework.common.util.http.bean;

/**
 * @author LIYANHUI
 */
public class HttpErrorDetail {

    private HttpRequest request;
    private Exception exception;

    public HttpErrorDetail() {
    }

    public HttpErrorDetail(HttpRequest request, Exception exception) {
        this.request = request;
        this.exception = exception;
    }

    public HttpRequest getRequest() {
        return this.request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public Exception getException() {
        return this.exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "HttpErrorDetail [request=" + this.request + ", exception=" + this.exception + "]";
    }
}
