package cn.cloudwalk.smartframework.rpc.invoke.service;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.AsyncInvokeProxy;
import cn.cloudwalk.smartframework.common.distributed.IRpcInvokeService;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;
import cn.cloudwalk.smartframework.common.distributed.provider.RpcServiceProvider;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.rpc.invoke.proxy.InvokeProxy;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public <F> F syncCall(Class<F> interfaceClass, String zookeeperId) {
        return syncCall(interfaceClass, zookeeperId, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <F> F syncCall(Class<F> interfaceClass, String zookeeperId, Class<?> returnType) {
        IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
        if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
            String className = interfaceClass.getName().replace(".", "/");
            String zookeeperPath = zookeeperService.getRpcServicePath() + "/" + zookeeperId + "/" + className;
            DistributedServiceProvider node = this.zookeeperService.getBestServiceProvider(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.RPC);
            if (node instanceof RpcServiceProvider) {
                return (F) Proxy.newProxyInstance(
                        interfaceClass.getClassLoader(),
                        new Class<?>[]{interfaceClass},
                        new InvokeProxy<>(node.getIp(), node.getPort(), interfaceClass, returnType)
                );
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("RPC invoke node error！"));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE not support rpc！"));
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
                return new InvokeProxy<>(node.getIp(), node.getPort(), interfaceClass, null);
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("RPC invoke node error！"));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE not support rpc！"));
        }
    }

    @Override
    public <F> List<F> syncBroadcast(Class<F> interfaceClass, String zookeeperId) {
        return syncBroadcast(interfaceClass, zookeeperId, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F> List<F> syncBroadcast(Class<F> interfaceClass, String zookeeperId, Class<?> returnType) {
        List<F> invokeProxyList = new ArrayList<>();
        IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
        if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
            String className = interfaceClass.getName().replace(".", "/");
            String zookeeperPath = zookeeperService.getRpcServicePath() + "/" + zookeeperId + "/" + className;
            List<DistributedServiceProvider> nodes = this.zookeeperService.getAvailableServiceList(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.RPC);
            if (nodes != null && !nodes.isEmpty()) {
                for (DistributedServiceProvider node : nodes) {
                    if (node instanceof RpcServiceProvider) {
                        invokeProxyList.add((F) Proxy.newProxyInstance(
                                interfaceClass.getClassLoader(),
                                new Class<?>[]{interfaceClass},
                                new InvokeProxy<>(node.getIp(), node.getPort(), interfaceClass, returnType)));
                    } else {
                        throw new FrameworkInternalSystemException(new SystemExceptionDesc("RPC invoke node error！"));
                    }
                }
                return new UnmodifiableList<>(invokeProxyList);
            } else {
                logger.error(zookeeperPath + " no available service provider, make sure that the service still has a running provider instance, or that the service address is correct");
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(zookeeperPath + " no available service provider, make sure that the service still has a running provider instance, or that the service address is correct"));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE not support rpc！"));
        }
    }

    @Override
    public <F> List<AsyncInvokeProxy> asyncBroadcast(Class<F> interfaceClass, String zookeeperId) {
        List<AsyncInvokeProxy> asyncInvokeProxyList = new ArrayList<>();
        IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
        if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
            String className = interfaceClass.getName().replace(".", "/");
            String zookeeperPath = zookeeperService.getRpcServicePath() + "/" + zookeeperId + "/" + className;
            List<DistributedServiceProvider> nodes = this.zookeeperService.getAvailableServiceList(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.RPC);
            if (nodes != null && !nodes.isEmpty()) {
                for (DistributedServiceProvider node : nodes) {
                    if (node instanceof RpcServiceProvider) {
                        asyncInvokeProxyList.add(new InvokeProxy<>(node.getIp(), node.getPort(), interfaceClass, null));
                    } else {
                        throw new FrameworkInternalSystemException(new SystemExceptionDesc("RPC invoke node error！"));
                    }
                }
                return new UnmodifiableList<>(asyncInvokeProxyList);
            } else {
                logger.error(zookeeperPath + " no available service provider, make sure that the service still has a running provider instance, or that the service address is correct");
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(zookeeperPath + " no available service provider, make sure that the service still has a running provider instance, or that the service address is correct"));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE not support rpc！"));
        }
    }
}
