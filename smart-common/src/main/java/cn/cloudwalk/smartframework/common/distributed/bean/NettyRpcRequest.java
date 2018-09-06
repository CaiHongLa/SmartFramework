package cn.cloudwalk.smartframework.common.distributed.bean;

import cn.cloudwalk.smartframework.common.model.BaseDataModel;

import java.util.Arrays;

/**
 * Rpc请求
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class NettyRpcRequest extends BaseDataModel {

    /**
     * 请求编号
     */
    private String requestId;

    /**
     * 是否单向请求
     */
    private boolean oneWay;

    /**
     * 请求接口名
     */
    private String className;

    /**
     * 请求参数类型接口
     */
    private Class<?>[] parameterTypes;

    /**
     * 请求参数集合
     */
    private Object[] parameters;

    /**
     * 请求方法名
     */
    private String methodName;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean getOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "NettyRpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", oneWay=" + oneWay +
                ", className='" + className + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
