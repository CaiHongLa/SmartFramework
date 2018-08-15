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
     * 请求参数类型接口
     */
    private Class<?>[] parameterTypes;

    /**
     * 请求参数集合
     */
    private Object[] parameters;

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
                "parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                "} ";
    }
}
