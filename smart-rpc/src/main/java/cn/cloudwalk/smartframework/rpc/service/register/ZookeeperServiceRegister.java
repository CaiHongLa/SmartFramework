package cn.cloudwalk.smartframework.rpc.service.register;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperRegister;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.provider.HttpServiceProvider;
import cn.cloudwalk.smartframework.common.distributed.provider.RpcServiceProvider;
import cn.cloudwalk.smartframework.rpc.bean.PublicHttpServiceVO;
import cn.cloudwalk.smartframework.rpc.bean.PublicRpcServiceVO;
import cn.cloudwalk.smartframework.rpc.netty.INettyRpcService;
import cn.cloudwalk.smartframework.rpc.service.holder.IPublicServiceHolder;
import cn.cloudwalk.smartframework.transport.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * ZookeeperServiceRegister
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("zookeeperServiceRegister")
public class ZookeeperServiceRegister extends BaseComponent implements IZookeeperRegister {
    private static final Logger logger = LogManager.getLogger(ZookeeperServiceRegister.class);

    @Autowired
    @Qualifier("publicServiceHolder")
    private IPublicServiceHolder publicServiceHolder;

    @Autowired
    @Qualifier("zookeeperService")
    private IZookeeperService zookeeperService;

    @Autowired
    @Qualifier("nettyRpcService")
    private INettyRpcService nettyRpcService;

    @Override
    public void registerService() {
        List<PublicHttpServiceVO> httpServiceVOS = this.publicServiceHolder.getAllHttpService();
        List<PublicRpcServiceVO> rpcServiceVOS = publicServiceHolder.getAllRpcService();
        if (rpcServiceVOS.isEmpty() && httpServiceVOS.isEmpty()) {
            logger.info("没有待注册的 zookeeper 服务，如果您确信有，请确认已添加 @PublicHttpService 或 @PublicRpcService 注解");
            return;
        }

        ServletContext context = getServletContext();
        if(null == context){
            logger.error("注意：当前应用没有部署在Servlet容器，因此所有的Http服务都将不进行注册！");
        }

        Properties config = this.zookeeperService.getZookeeperConfig();
        String zookeeperId = config.getProperty("zookeeper.id");
        String localIp = zookeeperService.getAvailableLocalIp();

        if (!rpcServiceVOS.isEmpty()) {
            String rpcServiceRootPath = this.zookeeperService.getRpcServicePath();
            String rpcRootPath = rpcServiceRootPath + "/" + zookeeperId + "/";
            Integer rpcPort = nettyRpcService.getHttpRpcPort();
            String rpcProviderPath = "/provider-" + localIp + ":" + rpcPort;
            Server server = nettyRpcService.getNettyRpcServer();
            if (server == null || server.isClosed()) {
                nettyRpcService.start();
                registerRpcService(rpcServiceVOS, rpcRootPath, rpcProviderPath, zookeeperId, localIp, rpcPort);
            } else {
                registerRpcService(rpcServiceVOS, rpcRootPath, rpcProviderPath, zookeeperId, localIp, rpcPort);
            }
        }

        if (!httpServiceVOS.isEmpty() && null != context) {
            logger.info("开始注册 Http 服务，共 " + httpServiceVOS.size() + " 个：" + httpServiceVOS);
            Integer httpPort = zookeeperService.getAvailableLocalPort();
            String httpProviderPath = "/provider-" + localIp + ":" + httpPort;
            String httpServiceRootPath = this.zookeeperService.getHttpServicePath();
            String httpRootPath = httpServiceRootPath + "/" + zookeeperId + "/";

            for (PublicHttpServiceVO httpServiceVO : httpServiceVOS) {
                String[] controllerMappingName = httpServiceVO.getControllerMappingName();
                if (controllerMappingName != null && controllerMappingName.length > 0) {
                    for (String controllerMapping : controllerMappingName) {
                        String[] methodMappingName = httpServiceVO.getMethodMappingName();
                        if (controllerMapping.startsWith("/")) {
                            controllerMapping = controllerMapping.substring(1);
                        }
                        for (String methodMapping : methodMappingName) {
                            if (!methodMapping.startsWith("/")) {
                                methodMapping = "/" + methodMapping;
                            }
                            String userPath = controllerMapping + methodMapping;
                            String nodePath = httpRootPath + userPath + httpProviderPath;
                            this.registerHttpServiceProvider(nodePath, controllerMapping, methodMapping, zookeeperId, localIp, httpPort);
                        }
                    }
                } else {
                    String[] methodMappingName = httpServiceVO.getMethodMappingName();
                    for (String methodMapping : methodMappingName) {
                        if (methodMapping.startsWith("/")) {
                            methodMapping = methodMapping.substring(1);
                        }
                        String nodePath = httpRootPath + methodMapping + httpProviderPath;
                        this.registerHttpServiceProvider(nodePath, null, methodMapping, zookeeperId, localIp, httpPort);
                    }
                }
            }
            logger.info("所有 Http 服务注册完成");
            logger.info("目前Zookeeper服务树如下：\n" + zookeeperService.getTreeInfoAsString(zookeeperService.getRootPath()));
        }
    }

    private void registerRpcService(List<PublicRpcServiceVO> rpcServiceVOS, String rpcRootPath, String rpcProviderPath,
                                    String zookeeperId, String localIp, Integer localPort) {
        logger.info("开始注册 Netty Rpc 服务，共 " + rpcServiceVOS.size() + " 个：" + rpcServiceVOS);
        for (PublicRpcServiceVO rpcServiceVO : rpcServiceVOS) {
            String className = rpcServiceVO.getClassName();
            className = className.replace(".", "/");
            String nodePath = rpcRootPath + className + rpcProviderPath;
            registerRpcServiceProvider(nodePath, className, zookeeperId, localIp, localPort);
        }
        logger.info("所有 Netty Rpc 服务注册完成");
        logger.info("目前Zookeeper服务树如下：\n" + zookeeperService.getTreeInfoAsString(zookeeperService.getRootPath()));
    }

    private void registerHttpServiceProvider(String registerPath, String controllerMapping, String methodMapping,
                                             String zookeeperId, String localIp, Integer localPort) {
        deleteExistPath(registerPath);
        HttpServiceProvider node = new HttpServiceProvider();
        node.setId(zookeeperId);
        node.setInstanceId(("instance_" + localIp + ":" + localPort).hashCode());
        node.setIp(localIp);
        node.setPort(localPort);
        node.setServiceComName(this.getServletContext().getContextPath().replaceAll("/", ""));
        node.setControllerMappingName(controllerMapping);
        node.setMethodMappingName(methodMapping);
        node.setRegisterTime(new Date());
        this.zookeeperService.registerService(registerPath, node);
    }

    private void registerRpcServiceProvider(String registerPath, String className,
                                            String zookeeperId, String localIp, Integer localPort) {
        deleteExistPath(registerPath);
        RpcServiceProvider node = new RpcServiceProvider();
        node.setClassName(className);
        node.setId(zookeeperId);
        node.setInstanceId(("instance_" + localIp + ":" + localPort).hashCode());
        node.setIp(localIp);
        node.setPort(localPort);
        node.setRegisterTime(new Date());
        this.zookeeperService.registerService(registerPath, node);
    }

    private void deleteExistPath(String registerPath) {
        if (this.zookeeperService.existPath(registerPath)) {
            this.zookeeperService.deletePath(registerPath);
            logger.info("zookeeper 服务节点（" + registerPath + "）已存在，将重新注册该节点，可能原因是服务组件的频繁重启或网络频繁中断，请注意观察服务组件以及网络的状况");
        }
    }


}
