package cn.cloudwalk.smartframework.common.util.http.async;

import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpErrorDetail;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午7:23
 * @since 2.0.10
 */
public class AsyncRpcExecuteResult {

    private ConcurrentMap<String, byte[]> result = new ConcurrentHashMap<>();
    private ConcurrentMap<String, HttpErrorDetail> errors = new ConcurrentHashMap<>();

    public AsyncRpcExecuteResult() {
    }

    public void addResult(String code, byte[] response) {
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

    public byte[] getFirst() {
        return this.result.size() > 0 ? this.result.values().iterator().next() : null;
    }

    public byte[] getResult(String code) {
        return this.result.get(code);
    }

    public Map<String, byte[]> getResults() {
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
        return "AsyncRpcExecuteResult [result=" + this.result + ", errors=" + this.errors + "]";
    }
}
