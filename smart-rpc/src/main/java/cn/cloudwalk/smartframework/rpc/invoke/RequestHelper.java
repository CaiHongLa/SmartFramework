package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponse;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;
import cn.cloudwalk.smartframework.common.util.HttpUtil;
import cn.cloudwalk.smartframework.common.util.JsonUtil;
import cn.cloudwalk.smartframework.common.util.ReflectUtil;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncCallbackAdapter;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncRpcCallBack;
import cn.cloudwalk.smartframework.common.util.http.bean.HTTP_CONTENT_TRANSFER_TYPE;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpRequest;
import cn.cloudwalk.smartframework.rpc.netty.codec.SerializationUtil;
import org.apache.http.StatusLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Rpc请求辅助类，用于发送Rpc请求以及处理请求结果
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/8/15
 * @since 2.0.10
 */
public final class RequestHelper {

    private static final Logger logger = LogManager.getLogger(RequestHelper.class);

    /**
     * 单向请求列表 方法返回类型包含在列表里 即为单向请求
     */
    private static final List<String> ONE_WAY_REQUEST_LIST = Arrays.asList("void", "Void");

    private RequestHelper() {

    }

    /**
     * @param ip          目标地址
     * @param port        目标端口
     * @param method      目标方法
     * @param objects     方法参数
     * @param clazz       目标接口地址
     * @param returnClazz 返回数据类型
     * @return Object Value
     * @throws Throwable Throwable
     */
    public static Object invokeRemote(String ip, int port, Method method, Object[] objects, Class<?> clazz, Class<?> returnClazz) throws Throwable {
        NettyRpcRequest request = new NettyRpcRequest();
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(objects);
        String returnClassName = method.getReturnType().getName();
        String requestClassName = clazz.getName().replace(".", "/");
        if (ONE_WAY_REQUEST_LIST.contains(returnClassName)) {
            sendRequestOneWay(ip, port, requestClassName, method.getName(), request);
            return new Object();
        }
        Class<?> returnClass;
        if (null == returnClazz) {
            returnClass = ReflectUtil.getClassType(returnClassName);
        } else {
            returnClass = returnClazz;
        }
        NettyRpcResponseFuture nettyRpcResponseFuture = sendRequest(ip, port, returnClass, requestClassName, method.getName(), request);
        NettyRpcResponse response = nettyRpcResponseFuture.get();
        Exception error = response.getError();
        if (error != null) {
            //服务方将异常包装成FrameworkInternalSystemException，这里直接抛出给调用方，由调用方处理
            throw error;
        }
        return response.getResult();
    }

    /**
     * 发送请求
     *
     * @param ip         目标地址
     * @param port       目标端口
     * @param type       返回数据类型
     * @param className  目标接口地址
     * @param methodName 目标方法
     * @param request    请求实体
     * @return V
     */
    @SuppressWarnings("unchecked")
    public static  NettyRpcResponseFuture sendRequest(String ip, int port, Class<?> type, String className, String methodName, NettyRpcRequest request) {
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
        HttpUtil.Async.post(new HttpRequest(url, params, HTTP_CONTENT_TRANSFER_TYPE.JSON), new AsyncCallbackAdapter() {
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
