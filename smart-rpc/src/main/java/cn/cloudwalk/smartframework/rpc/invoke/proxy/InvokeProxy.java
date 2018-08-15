package cn.cloudwalk.smartframework.rpc.invoke.proxy;

import cn.cloudwalk.smartframework.common.distributed.AsyncInvokeProxy;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponseFuture;
import cn.cloudwalk.smartframework.common.util.ReflectUtil;
import cn.cloudwalk.smartframework.rpc.invoke.RequestHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * 反射方式调用RPC服务
 *
 * @author 李延辉
 * @since 1.0.0
 */
public class InvokeProxy<T> implements InvocationHandler, AsyncInvokeProxy {

    private String ip;
    private Integer port;
    private Class<T> clazz;
    private Class<?> returnClazz;

    public InvokeProxy(String ip, Integer port, Class<T> clazz,Class<?> returnClazz) {
        this.ip = ip;
        this.port = port;
        this.clazz = clazz;
        this.returnClazz = returnClazz;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return RequestHelper.invokeRemote(ip, port, method, objects, clazz, returnClazz);
    }

    @Override
    public <V> NettyRpcResponseFuture<V> asyncCall(Class<V> type, String methodName, Object[] args) {
        NettyRpcRequest request = createRequest(args);
        return RequestHelper.sendRequest(ip, port, type, clazz.getName().replace(".", "/"), methodName, request);
    }

    @Override
    public void asyncOneWayCall(String methodName, Object[] args) {
        NettyRpcRequest request = createRequest(args);
        RequestHelper.sendRequestOneWay(ip, port, clazz.getName().replace(".", "/"), methodName, request);
    }

    private NettyRpcRequest createRequest(Object[] args) {
        NettyRpcRequest request = new NettyRpcRequest();
        request.setParameters(args);
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = ReflectUtil.getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
        return request;
    }
}
