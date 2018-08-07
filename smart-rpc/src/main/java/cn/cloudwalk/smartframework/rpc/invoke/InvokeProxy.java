package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.util.HttpUtil;
import cn.cloudwalk.smartframework.common.util.JsonUtil;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncCallbackAdapter;
import cn.cloudwalk.smartframework.common.util.http.bean.HTTP_CONTENT_TRANSFER_TYPE;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpRequest;
import cn.cloudwalk.smartframework.rpc.invoke.future.FutureSet;
import cn.cloudwalk.smartframework.rpc.invoke.future.NettyRpcResponseFuture;
import cn.cloudwalk.smartframework.rpc.netty.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.rpc.netty.bean.NettyRpcResponse;
import cn.cloudwalk.smartframework.rpc.netty.codec.SerializationUtil;
import org.apache.http.StatusLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;


/**
 * 反射方式调用RPC服务
 *
 * @author 李延辉
 * @since 1.0.0
 */
public class InvokeProxy<T> implements InvocationHandler, AsyncInvokeProxy {

    private static final Logger logger = LogManager.getLogger(InvokeProxy.class);
    private String ip;
    private Integer port;
    private Class<T> clazz;
    /**
     * 单向请求列表 方法返回类型包含在列表里 即为单向请求
     */
    private static final List<String> ONE_WAY_REQUEST_LIST = Arrays.asList("void", "Void");

    public InvokeProxy(String ip, Integer port, Class<T> clazz) {
        this.ip = ip;
        this.port = port;
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        NettyRpcRequest request = new NettyRpcRequest();
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(objects);
        String returnClassName = method.getReturnType().getName();
        String requestClassName = method.getDeclaringClass().getName().replace(".", "/");
        if(ONE_WAY_REQUEST_LIST.contains(returnClassName)){
            sendRequestOneWay(requestClassName, method.getName(), request);
            return new Object();
        }
        Class<?> returnClass = Class.forName(returnClassName);
        NettyRpcResponseFuture nettyRpcResponseFuture = sendRequest(returnClass, requestClassName, method.getName(), request);
        NettyRpcResponse response = nettyRpcResponseFuture.get();
        Exception error = response.getError();
        if (error != null) {
            //服务方将异常包装成FrameworkInternalSystemException，这里直接抛出给调用方，由调用方处理
            throw error;
        }
        return response.getValue();
    }

    @Override
    public <V> NettyRpcResponseFuture<V> asyncCall(Class<V> type, String methodName, Object[] args) {
        NettyRpcRequest request = createRequest(args);
        return sendRequest(type, clazz.getName().replace(".", "/"), methodName, request);
    }

    @Override
    public void asyncOneWayCall(String methodName, Object[] args) {
        NettyRpcRequest request = createRequest(args);
        sendRequestOneWay(clazz.getName().replace(".", "/"), methodName, request);
    }

    private NettyRpcRequest createRequest(Object[] args) {
        NettyRpcRequest request = new NettyRpcRequest();
        request.setParameters(args);
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
        return request;
    }

    private String buildUrl(String className, String methodName, String requestId, boolean oneWay){
        return "http://" + ip + ":" + port + "/" + className + "?requestId=" + requestId + "&methodName=" + methodName + "&oneWay=" + oneWay;
    }

    private String buildRequestId(){
        return UUID.randomUUID().toString();
    }

    private Map<String, byte[]> buildParam(NettyRpcRequest request){
        byte[] data = SerializationUtil.serialize(request);
        Map<String, byte[]> params = new HashMap<>();
        params.put("data", data);
        return params;
    }

    @SuppressWarnings("unchecked")
    private <V> NettyRpcResponseFuture<V> sendRequest(Class<V> type, String className, String methodName, NettyRpcRequest request) {
        final String requestId = buildRequestId();
        final String url = buildUrl(className, methodName, requestId, false);
        logger.info("准备发起一个请求：" + url);
        logger.info("请求参数：" + request);
        NettyRpcResponseFuture<V> nettyRpcResponseFuture = new NettyRpcResponseFuture<>(requestId, className, methodName);
        FutureSet.futureMap.put(requestId, nettyRpcResponseFuture);
        Map<String, byte[]> params = buildParam(request);
        HttpUtil.Async.post(new HttpRequest(url, params, HTTP_CONTENT_TRANSFER_TYPE.JSON), new AsyncCallbackAdapter() {

            @Override
            public void onError(Exception e, HttpRequest metadata) {
                logger.error("请求异常", e);
                NettyRpcResponseFuture<V> future = FutureSet.futureMap.get(requestId);
                if (future != null) {
                    FutureSet.futureMap.remove(requestId);
                    NettyRpcResponse<V> response = new NettyRpcResponse<>();
                    response.setRequestId(requestId);
                    response.setError(e);
                    future.done(response);
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onComplete(String responseText, HttpRequest metadata, StatusLine status) {
                logger.info("请求响应结果：" + responseText);
                NettyRpcResponse<V> response = JsonUtil.json2Object(responseText, NettyRpcResponse.class);
                Object result = response.getResult();
                if(result != null){
                    response.setValue(JsonUtil.json2Object(JsonUtil.object2Json(result), type));
                }
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

    private void sendRequestOneWay(String className, String methodName, NettyRpcRequest request) {
        final String requestId = buildRequestId();
        final String url = buildUrl(className, methodName, requestId, true);
        logger.info("准备发起一个单向请求：" + url);
        logger.info("单向请求参数：" + request);
        Map<String, byte[]> params = buildParam(request);
        HttpUtil.Async.post(new HttpRequest(url, params, HTTP_CONTENT_TRANSFER_TYPE.JSON), new AsyncCallbackAdapter() {
            @Override
            public void onError(Exception e, HttpRequest metadata) {
                logger.error("单向请求异常", e);
            }
        });
    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
            default:
                return classType;
        }
    }
}
