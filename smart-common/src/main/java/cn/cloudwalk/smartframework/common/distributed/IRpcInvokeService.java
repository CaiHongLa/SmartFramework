package cn.cloudwalk.smartframework.common.distributed;

/**
 * Rpc远程调用
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IRpcInvokeService {

    <F> F syncCall(Class<F> interfaceClass, String zookeeperId);
}
