package cn.cloudwalk.smartframework.rpc.invoke.proxy;

import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;
import cn.cloudwalk.smartframework.common.distributed.provider.RpcServiceProvider;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.rpc.invoke.RequestHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 自动注解代理类
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/8/15
 * @since 2.0.10
 */
public class AutoInvokeProxy <T> implements InvocationHandler {

    private Class<T> clazz;
    private Class<?> returnClazz;
    private String zookeeperId;
    private IZookeeperService zookeeperService;

    public AutoInvokeProxy(Class<T> clazz, String zookeeperId,  Class<?> returnClazz, IZookeeperService zookeeperService) {
        this.zookeeperId = zookeeperId;
        this.clazz = clazz;
        this.returnClazz = returnClazz;
        this.zookeeperService = zookeeperService;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        IZookeeperService.RUNNING_MODE runningMode = zookeeperService.getRunningMode();
        String ip;
        int port;
        if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
            String className = clazz.getName().replace(".", "/");
            String zookeeperPath = zookeeperService.getRpcServicePath() + "/" + zookeeperId + "/" + className;
            DistributedServiceProvider node = zookeeperService.getBestServiceProvider(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.RPC);
            if (node instanceof RpcServiceProvider) {
                ip = node.getIp();
                port = node.getPort();
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("RPC invoke node error！"));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE model cannot use rpc invoke！"));
        }

        return RequestHelper.invokeRemote(ip, port, method, objects, clazz, returnClazz);
    }
}
