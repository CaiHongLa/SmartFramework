package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;
import cn.cloudwalk.smartframework.common.distributed.provider.RpcServiceProvider;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * AutowiredService代理类
 * <p>
 * 2018/8/18 10:36
 *
 * @author liyanhui(liyanhui @ cloudwalk.cn)
 * @since 2.0.10
 */
public class AutowiredServiceInvocationHandler implements InvocationHandler {

    private final RpcInvoker<?> invoker;

    public AutowiredServiceInvocationHandler(RpcInvoker<?> invoker) {
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        IZookeeperService.RUNNING_MODE runningMode = invoker.getZookeeperService().getRunningMode();
        String ip;
        int port;
        if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
            String className = invoker.getInterface().getName();
            String zookeeperPath = invoker.getZookeeperService().getRpcServicePath() + "/" + invoker.getZookeeperId() + "/" + className.replace(".", "/");
            DistributedServiceProvider node = invoker.getZookeeperService().getBestServiceProvider(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.RPC);
            if (node instanceof RpcServiceProvider) {
                ip = node.getIp();
                port = node.getPort();
                RpcInvocation invocation = new RpcInvocation(ip, port, className, method, args);
                RpcResult result = invoker.invoke(invocation);
                if (invoker.isAsync()) {
                    String returnClassName = method.getReturnType().getName();
                    switch (returnClassName) {
                        case "int":
                            return 0;
                        case "double":
                            return 0.0;
                        case "float":
                            return 0f;
                        case "short":
                            return Short.valueOf("0");
                        case "long":
                            return 0L;
                        case "boolean":
                            return false;
                        case "byte":
                            return (byte) 0;
                        case "char":
                            return 'O';
                        default:
                            return null;
                    }
                }
                return result.getValueIfHasException();
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("RPC invoke node error！"));
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE model cannot use rpc invoke！"));
        }
    }

}
