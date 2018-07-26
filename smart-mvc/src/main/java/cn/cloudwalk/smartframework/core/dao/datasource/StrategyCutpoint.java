package cn.cloudwalk.smartframework.core.dao.datasource;

import java.lang.reflect.Method;

/**
 * @author LIYANHUI
 */
public class StrategyCutpoint {

    private String methodName;
    private Method method;
    private Object[] args;
    private String className;
    private Object target;
    private DataSourceHolder holder;

    public StrategyCutpoint(String methodName, Method method, Object[] args, String className, Object target, DataSourceHolder holder) {
        this.methodName = methodName;
        this.method = method;
        this.args = args;
        this.className = className;
        this.target = target;
        this.holder = holder;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public DataSourceHolder getHolder() {
        return this.holder;
    }

    public void setHolder(DataSourceHolder holder) {
        this.holder = holder;
    }

}
