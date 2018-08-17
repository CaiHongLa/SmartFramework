package cn.cloudwalk.smartframework.common.distributed.bean;

import cn.cloudwalk.smartframework.common.model.BaseDataModel;

/**
 * Rpc返回
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class NettyRpcResponse extends BaseDataModel {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 请求异常
     */
    private Exception error;

    /**
     * 请求结果--json
     */
    private Object result;


    public boolean isError() {
        return error != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }


    @Override
    public String toString() {
        return "NettyRpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", error=" + error +
                ", result=" + result +
                "} ";
    }
}
