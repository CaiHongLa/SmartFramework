package cn.cloudwalk.smartframework.rpc.invoke.service;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IRpcInvokeService;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.rpc.invoke.AutowiredServiceInvocationHandler;
import cn.cloudwalk.smartframework.rpc.invoke.RpcInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * RpcInvokeService
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("rpcInvokeService")
public class RpcInvokeService extends BaseComponent implements IRpcInvokeService {

    @Autowired
    @Qualifier("zookeeperService")
    private IZookeeperService zookeeperService;

    @SuppressWarnings("unchecked")
    @Override
    public <F> F syncCall(Class<F> interfaceClass, String zookeeperId) {
        IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
        if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
            RpcInvoker<?> invoker = new RpcInvoker<>(interfaceClass, zookeeperId, zookeeperService, false);
            return (F) Proxy.newProxyInstance(
                    interfaceClass.getClassLoader(),
                    new Class<?>[]{interfaceClass},
                    new AutowiredServiceInvocationHandler(invoker));
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE not support rpc！"));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F> F asyncCall(Class<F> interfaceClass, String zookeeperId) {
        IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
        if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
            RpcInvoker<?> invoker = new RpcInvoker<>(interfaceClass, zookeeperId, zookeeperService, true, false);
            return (F) Proxy.newProxyInstance(
                    interfaceClass.getClassLoader(),
                    new Class<?>[]{interfaceClass},
                    new AutowiredServiceInvocationHandler(invoker));
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE not support rpc！"));
        }
    }

}
