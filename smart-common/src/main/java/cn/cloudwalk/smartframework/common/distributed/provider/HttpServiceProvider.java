package cn.cloudwalk.smartframework.common.distributed.provider;


import java.util.Date;

/**
 * Http服务提供者
 *
 * @author LIYANHUI
 * @see cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider
 * @since 1.0.0
 */
public class HttpServiceProvider extends DistributedServiceProvider {

    /**
     * Controller类的RequestMapping注解
     */
    private String controllerMappingName;

    /**
     * Controller类的方法的RequestMapping注解
     */
    private String methodMappingName;

    /**
     * web应用项目名称
     */
    private String serviceComName;

    public String getServiceComName() {
        return this.serviceComName;
    }

    public void setServiceComName(String serviceComName) {
        this.serviceComName = serviceComName;
    }

    public HttpServiceProvider() {
    }

    public HttpServiceProvider(String id, Integer instanceId, String ip, Integer port, String serviceComName, String controllerMappingName, String methodMappingName, Date registTime) {
        this.setId(id);
        this.setInstanceId(instanceId);
        this.setIp(ip);
        this.setPort(port);
        this.setServiceComName(serviceComName);
        this.controllerMappingName = controllerMappingName;
        this.methodMappingName = methodMappingName;
        this.setRegisterTime(registTime);
    }


    public String getControllerMappingName() {
        return this.controllerMappingName;
    }

    public void setControllerMappingName(String controllerMappingName) {
        this.controllerMappingName = controllerMappingName;
    }

    public String getMethodMappingName() {
        return this.methodMappingName;
    }

    public void setMethodMappingName(String methodMappingName) {
        this.methodMappingName = methodMappingName;
    }

    public String buildUrl() {
        return "http://" + this.getIp() + ":" + this.getPort() + "/" + this.getServiceComName() + "/" + (this.controllerMappingName != null ? this.controllerMappingName + "/" : "") + this.methodMappingName;
    }

    @Override
    public String toString() {
        return "HttpServiceProvider{" +
                "controllerMappingName='" + controllerMappingName + '\'' +
                ", methodMappingName='" + methodMappingName + '\'' +
                ", serviceComName='" + serviceComName + '\'' +
                "} " + super.toString();
    }
}
