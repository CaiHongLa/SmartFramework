package cn.cloudwalk.smartframework.common.distributed;

/**
 * Rpc异步调用回调接口
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface AsyncCallBack<V> {

    void onSuccess(V result);

    void onError(Exception e);
}
