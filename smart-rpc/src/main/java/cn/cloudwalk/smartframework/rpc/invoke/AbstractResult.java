package cn.cloudwalk.smartframework.rpc.invoke;

/**
 * 抽象Result
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午6:19
 * @since 2.0.10
 */
public abstract class AbstractResult implements Result{

    protected Object value;
    protected Throwable exception;
}
