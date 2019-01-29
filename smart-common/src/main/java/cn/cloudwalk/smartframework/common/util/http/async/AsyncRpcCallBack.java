package cn.cloudwalk.smartframework.common.util.http.async;

import cn.cloudwalk.smartframework.common.util.http.bean.HttpRequest;
import org.apache.http.StatusLine;

/**
 * Rpc调用回调
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午7:21
 * @since 2.0.10
 */
public interface AsyncRpcCallBack {

    void onComplete(byte[] data, HttpRequest metadata, StatusLine status);

    void onError(Exception e, HttpRequest metadata);
}
