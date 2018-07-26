package cn.cloudwalk.smartframework.common.distributed.provider;

/**
 * Rpc服务提供者
 *
 * @author LIYANHUI
 * @see cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider
 * @since 1.0.0
 */
public class RpcServiceProvider extends DistributedServiceProvider {

    /**
     * Rpc服务类接口
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
        return "RpcServiceProvider{" +
                "className='" + className + '\'' +
                "} " + super.toString();
    }
}
