package cn.cloudwalk.smartframework.rpc.invoke;

import java.util.List;

/**
 * Rpc远程调用
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IRpcInvokeService {

    <F> F syncCall(Class<F> interfaceClass, String zookeeperId);

    <F> F syncCall(Class<F> interfaceClass, String zookeeperId, Class<?> returnType);

    <F> AsyncInvokeProxy asyncCall(Class<F> interfaceClass, String zookeeperId);

    <F> AsyncInvokeProxy asyncCall(Class<F> interfaceClass, String zookeeperId, Class<?> returnType);

    <F> List<F> syncBroadcast(Class<F> interfaceClass, String zookeeperId);

    <F> List<F> syncBroadcast(Class<F> interfaceClass, String zookeeperId, Class<?> returnType);

    <F> List<AsyncInvokeProxy> asyncBroadcast(Class<F> interfaceClass, String zookeeperId);

    <F> List<AsyncInvokeProxy> asyncBroadcast(Class<F> interfaceClass, String zookeeperId, Class<?> returnType);

}
