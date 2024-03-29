package cn.cloudwalk.smartframework.rpc.invoke;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Rpc反射实体
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午6:24
 * @since 2.0.10
 */
class RpcInvocation {

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private String ip;

    private int port;

    private boolean oneWay = false;

    private static final List<String> ONE_WAY_REQUEST_LIST = Arrays.asList("void", "Void");

    RpcInvocation(){}

    RpcInvocation(String ip, int port, String className, Method method, Object[] arguments) {
        this.className = className;
        this.methodName = method.getName();
        String returnClassName = method.getReturnType().getName();
        if (ONE_WAY_REQUEST_LIST.contains(returnClassName)) {
            this.oneWay = true;
        }
        this.parameterTypes = method.getParameterTypes();
        this.arguments = arguments == null ? new Object[0] : arguments;
        this.ip = ip;
        this.port = port;
    }

    RpcInvocation setClassName(String className) {
        this.className = className;
        return this;
    }

    RpcInvocation setMethod(Method method) {
        this.methodName = method.getName();
        String returnClassName = method.getReturnType().getName();
        if (ONE_WAY_REQUEST_LIST.contains(returnClassName)) {
            this.oneWay = true;
        }
        this.parameterTypes = method.getParameterTypes();
        return this;
    }

    RpcInvocation setArguments(Object[] arguments) {
        this.arguments = arguments == null ? new Object[0] : arguments;
        return this;
    }

    RpcInvocation setIp(String ip) {
        this.ip = ip;
        return this;
    }

    RpcInvocation setPort(int port) {
        this.port = port;
        return this;
    }

    String getClassName() {
        return className;
    }

    String getMethodName() {
        return methodName;
    }

    boolean isOneWay() {
        return oneWay;
    }

    Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    Object[] getArguments() {
        return arguments;
    }

    String getTargetIp() {
        return ip;
    }

    int getTargetPort() {
        return port;
    }

    @Override
    public String toString() {
        return "RpcInvocation{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", arguments=" + Arrays.toString(arguments) +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", oneWay=" + oneWay +
                '}';
    }
}
