package cn.cloudwalk.smartframework.common.util.http.async;

import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpErrorDetail;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author LIYANHUI
 */
public class AsyncExecuteResult {
    private ConcurrentMap<String, HttpResponse> result = new ConcurrentHashMap<>();
    private ConcurrentMap<String, HttpErrorDetail> errors = new ConcurrentHashMap<>();

    public AsyncExecuteResult() {
    }

    public void addResult(String code, HttpResponse response) {
        if (code == null) {
            code = TextUtil.generateUUID();
        }

        this.result.put(code, response);
    }

    public void addError(String code, HttpErrorDetail errorDetail) {
        if (code == null) {
            code = TextUtil.generateUUID();
        }

        this.errors.put(code, errorDetail);
    }

    public HttpResponse getFirst() {
        return this.result.size() > 0 ? this.result.values().iterator().next() : null;
    }

    public HttpResponse getResult(String code) {
        return this.result.get(code);
    }

    public Map<String, HttpResponse> getResults() {
        return this.result;
    }

    public boolean hasErrors() {
        return this.errors.size() > 0;
    }

    public int getErrorCount() {
        return this.errors.size();
    }

    public Map<String, HttpErrorDetail> getErrors() {
        return this.errors;
    }

    @Override
    public String toString() {
        return "AsyncExecuteResult [result=" + this.result + ", errors=" + this.errors + "]";
    }
}