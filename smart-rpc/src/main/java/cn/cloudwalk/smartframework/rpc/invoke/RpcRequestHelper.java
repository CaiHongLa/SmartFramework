package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponse;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;
import cn.cloudwalk.smartframework.common.util.HttpUtil;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncRpcCallBack;
import cn.cloudwalk.smartframework.common.util.http.bean.HTTP_CONTENT_TRANSFER_TYPE;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpRequest;
import cn.cloudwalk.smartframework.rpc.netty.codec.SerializationUtil;
import org.apache.http.StatusLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Rpc请求辅助类，用于发送Rpc请求以及处理请求结果
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/8/15
 * @since 2.0.10
 */
public final class RpcRequestHelper {

    private static final Logger logger = LogManager.getLogger(RpcRequestHelper.class);

    private RpcRequestHelper() {

    }

    /**
     * 发送请求
     *
     * @param ip         目标地址
     * @param port       目标端口
     * @param className  目标接口地址
     * @param methodName 目标方法
     * @param request    请求实体
     * @return V
     */
    @SuppressWarnings("unchecked")
    public static NettyRpcResponseFuture sendRequest(String ip, int port, String className, String methodName, NettyRpcRequest request) {
        final String requestId = buildRequestId();
        final String url = buildUrl(ip, port, className, methodName, requestId, false);
        logger.info("ready to send a request：" + url);
        logger.info("request params：" + request);
        NettyRpcResponseFuture nettyRpcResponseFuture = new NettyRpcResponseFuture(requestId, className, methodName);
        FutureSet.futureMap.put(requestId, nettyRpcResponseFuture);
        Map<String, byte[]> params = buildParam(request);
        HttpUtil.Async.postRpc(new HttpRequest(url, params, HTTP_CONTENT_TRANSFER_TYPE.JSON), new AsyncRpcCallBack() {

            @Override
            public void onError(Exception e, HttpRequest metadata) {
                logger.error("request error", e);
                NettyRpcResponseFuture future = FutureSet.futureMap.get(requestId);
                if (future != null) {
                    FutureSet.futureMap.remove(requestId);
                    NettyRpcResponse response = new NettyRpcResponse();
                    response.setRequestId(requestId);
                    response.setError(e);
                    future.done(response);
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onComplete(byte[] data, HttpRequest metadata, StatusLine status) {
                NettyRpcResponse response = SerializationUtil.deserialize(data, NettyRpcResponse.class);
                logger.info("request result：" + response);
                String requestId = response.getRequestId();
                NettyRpcResponseFuture future = FutureSet.futureMap.get(requestId);
                if (future != null) {
                    FutureSet.futureMap.remove(requestId);
                    future.done(response);
                }
            }
        });
        return nettyRpcResponseFuture;
    }

    /**
     * 发送单向请求 不处理返回结果
     *
     * @param ip         目标地址
     * @param port       目标端口
     * @param className  目标接口地址
     * @param methodName 目标方法
     * @param request    请求实体
     */
    public static void sendRequestOneWay(String ip, int port, String className, String methodName, NettyRpcRequest request) {
        final String requestId = buildRequestId();
        final String url = buildUrl(ip, port, className, methodName, requestId, true);
        logger.info("ready to send an one way request：" + url);
        logger.info("one way request params：" + request);
        Map<String, byte[]> params = buildParam(request);
        HttpUtil.Async.postRpc(new HttpRequest(url, params, HTTP_CONTENT_TRANSFER_TYPE.JSON), new AsyncRpcCallBack() {
            @Override
            public void onComplete(byte[] data, HttpRequest metadata, StatusLine status) {
                logger.info("one way request completed");
            }

            @Override
            public void onError(Exception e, HttpRequest metadata) {
                logger.error("one way request error", e);
            }
        });
    }

    private static String buildUrl(String ip, int port, String className, String methodName, String requestId, boolean oneWay) {
        return "http://" + ip + ":" + port + "/" + className + "?requestId=" + requestId + "&methodName=" + methodName + "&oneWay=" + oneWay;
    }

    private static String buildRequestId() {
        return UUID.randomUUID().toString();
    }

    private static Map<String, byte[]> buildParam(NettyRpcRequest request) {
        byte[] data = SerializationUtil.serialize(request);
        Map<String, byte[]> params = new HashMap<>();
        params.put("data", data);
        return params;
    }

}
