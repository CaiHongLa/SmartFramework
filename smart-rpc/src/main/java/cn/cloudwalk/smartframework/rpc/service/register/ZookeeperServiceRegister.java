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
            logger.info("There is no zookeeper service to be registered, and if you are sure, make sure you have added the @PublicHttpService or @PublicRpcService annotation");
            return;
        }

        ServletContext context = getServletContext();
        if(null == context){
            logger.error("Note: The current application is not deployed in the Servlet container, so all Http services will not be registered!");
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
            logger.info("registering Http services, a total of " + httpServiceVOS.size() + " , they are " + httpServiceVOS);
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
            logger.info("All Http services are registered.");
            logger.info("At present, the Zookeeper service tree is as follows：\n" + zookeeperService.getTreeInfoAsString(zookeeperService.getRootPath()));
        }
    }

    private void registerRpcService(List<PublicRpcServiceVO> rpcServiceVOS, String rpcRootPath, String rpcProviderPath,
                                    String zookeeperId, String localIp, Integer localPort) {
        logger.info("registering Rpc services，a total of " + rpcServiceVOS.size() + " ,they are" + rpcServiceVOS);
        for (PublicRpcServiceVO rpcServiceVO : rpcServiceVOS) {
            String className = rpcServiceVO.getClassName();
            className = className.replace(".", "/");
            String nodePath = rpcRootPath + className + rpcProviderPath;
            registerRpcServiceProvider(nodePath, className, zookeeperId, localIp, localPort);
        }
        logger.info("All rpc services are registered.");
        logger.info("At present, the Zookeeper service tree is as follows：\n" + zookeeperService.getTreeInfoAsString(zookeeperService.getRootPath()));
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
            logger.info("zookeeper node（" + registerPath + "）exist，this node will be re-registered, possibly due to frequent restarts of the service component or frequent network interruptions. Note the status of the service component and the network");
        }
    }


}
