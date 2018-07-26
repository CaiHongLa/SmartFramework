package cn.cloudwalk.smartframework.rpc.invoke.service;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;
import cn.cloudwalk.smartframework.common.distributed.provider.RpcServiceProvider;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.rpc.invoke.AsyncInvokeProxy;
import cn.cloudwalk.smartframework.rpc.invoke.IRpcInvokeService;
import cn.cloudwalk.smartframework.rpc.invoke.InvokeProxy;
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
            String className = interfaceClass.getName().replace(".", "/");
            String zookeeperPath = zookeeperService.getRpcServicePath() + "/" + zookeeperId + "/" + className;
            DistributedServiceProvider node = this.zookeeperService.getBestServiceProvider(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.RPC);
            if (node instanceof RpcServiceProvider) {
                return (F) Proxy.newProxyInstance(
                        interfaceClass.getClassLoader(),
                        new Class<?>[]{interfaceClass},
                        new InvokeProxy<>(node.getIp(), node.getPort(), interfaceClass)
                );
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("RPC调用方式获取节点错误！"));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE模式下不能启用RPC调用！"));
        }

    }

    @Override
    public <F> AsyncInvokeProxy asyncCall(Class<F> interfaceClass, String zookeeperId) {
        IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
        if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
            String className = interfaceClass.getName().replace(".", "/");
            String zookeeperPath = zookeeperService.getRpcServicePath() + "/" + zookeeperId + "/" + className;
            DistributedServiceProvider node = this.zookeeperService.getBestServiceProvider(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.RPC);
            if (node instanceof RpcServiceProvider) {
                return new InvokeProxy<>(node.getIp(), node.getPort(), interfaceClass);
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("RPC调用方式获取节点错误！"));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE模式下不能启用RPC调用！"));
        }
    }
}
