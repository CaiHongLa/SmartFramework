package cn.cloudwalk.smartframework.rpc.invoke;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Rpc反射实体
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午6:24
 * @since 2.0.10
 */
public class RpcInvocation implements Invocation, Serializable {

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private String ip;

    private int port;

    public RpcInvocation(Invocation invocation) {
        this(invocation.getTargetIp(), invocation.getTargetPort(), invocation.getClassName(), invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
    }

    public RpcInvocation(String ip, int port, String className, Method method, Object[] arguments) {
        this(ip, port, className, method.getName(), method.getParameterTypes(), arguments);
    }

    public RpcInvocation(String ip, int port, String className, String methodName, Class<?>[] parameterTypes, Object[] arguments) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
        this.arguments = arguments == null ? new Object[0] : arguments;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public String getTargetIp() {
        return ip;
    }

    @Override
    public int getTargetPort() {
        return port;
    }

}
