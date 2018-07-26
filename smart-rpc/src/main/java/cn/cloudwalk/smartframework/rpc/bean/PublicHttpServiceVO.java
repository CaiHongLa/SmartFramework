package cn.cloudwalk.smartframework.rpc.bean;

import cn.cloudwalk.smartframework.rpc.annotation.PublicHttpService;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 持有一个被注解了{@link PublicHttpService} 的服务的全部属性。
 * 代表一个可以注册的Http服务。
 *
 * @author 李延辉
 * @see PublicHttpService
 * @since 1.0.0
 */
public class PublicHttpServiceVO {

    /**
     * controller类的{@link org.springframework.web.bind.annotation.RequestMapping} 注解内容
     */
    private String[] controllerMappingName;

    /**
     * controller类的类名称
     */
    private String controllerClassName;

    /**
     * 类中某个注解了{@link org.springframework.web.bind.annotation.RequestMapping}的方法的注解内容
     */
    private String[] methodMappingName;

    /**
     * 类中某个注解了{@link org.springframework.web.bind.annotation.RequestMapping}的方法
     */
    private Method method;

    public String[] getControllerMappingName() {
        return this.controllerMappingName;
    }

    public void setControllerMappingName(String[] controllerMappingName) {
        this.controllerMappingName = controllerMappingName;
    }

    public String getControllerClassName() {
        return this.controllerClassName;
    }

    public void setControllerClassName(String controllerClassName) {
        this.controllerClassName = controllerClassName;
    }

    public String[] getMethodMappingName() {
        return this.methodMappingName;
    }

    public void setMethodMappingName(String[] methodMappingName) {
        this.methodMappingName = methodMappingName;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "PublicHttpServiceVO{" +
                "controllerMappingName=" + Arrays.toString(controllerMappingName) +
                ", controllerClassName='" + controllerClassName + '\'' +
                ", methodMappingName=" + Arrays.toString(methodMappingName) +
                ", method=" + method +
                '}';
    }
}
