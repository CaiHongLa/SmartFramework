package cn.cloudwalk.smartframework.rpc.invoke;

/**
 * Rpc远程调用
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IRpcInvokeService {

    <F> F syncCall(Class<F> interfaceClass, String zookeeperId);

    <F> AsyncInvokeProxy asyncCall(Class<F> interfaceClass, String zookeeperId);
}
