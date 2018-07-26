package cn.cloudwalk.smartframework.rpc.bean;

import cn.cloudwalk.smartframework.rpc.annotation.PublicRpcService;

/**
 * 持有一个被注解了{@link PublicRpcService} 的服务的全部属性。
 * 代表一个可以注册的Rpc服务。
 *
 * @author 李延辉
 * @see PublicRpcService
 * @since 1.0.0
 */
public class PublicRpcServiceVO {

    /**
     * 注解的那个类的类名称
     */
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "PublicRpcServiceVO{" +
                "className='" + className + '\'' +
                '}';
    }
}
