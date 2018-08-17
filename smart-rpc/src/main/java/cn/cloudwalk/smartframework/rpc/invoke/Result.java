package cn.cloudwalk.smartframework.rpc.invoke;

import java.io.Serializable;

/**
 * Rpc调用结果
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-17 下午5:59
 * @since 2.0.10
 */
public interface Result extends Serializable {

    /**
     * 获取调用结果
     *
     * @return 结果 如果没有 返回null
     */
    Object getValue();

    /**
     * 获取调用异常
     *
     * @return 异常 如果没有 返回 null
     */
    Throwable getException();

    /**
     * 是否异常
     *
     * @return 是否异常
     */
    boolean hasException();

    /**
     * 获取结果 如果有异常 直接抛异常
     *
     * @return getValue
     * @throws Throwable if has
     */
    Object getValueIfHasException() throws Throwable;
}
