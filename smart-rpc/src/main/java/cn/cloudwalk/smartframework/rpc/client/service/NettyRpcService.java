package cn.cloudwalk.smartframework.rpc.client.service;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.rpc.client.RpcProtocol;
import cn.cloudwalk.smartframework.rpc.client.RpcRequestHandler;
import cn.cloudwalk.smartframework.rpc.client.RpcTransport;
import cn.cloudwalk.smartframework.rpc.service.holder.IPublicServiceHolder;
import cn.cloudwalk.smartframework.transportcomponents.Protocol;
import cn.cloudwalk.smartframework.transportcomponents.Server;
import cn.cloudwalk.smartframework.transportcomponents.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transportcomponents.support.transport.TransportException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Rpc服务启动
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("nettyRpcService")
public class NettyRpcService extends BaseComponent implements INettyRpcService {
    private static final Logger logger = LogManager.getLogger(NettyRpcService.class);

    @Autowired
    @Qualifier("zookeeperService")
    private IZookeeperService zookeeperService;

    @Autowired
    @Qualifier("publicServiceHolder")
    private IPublicServiceHolder publicServiceHolder;

    private Map<String, String> parameters = new HashMap<>(50);

    private Integer port;
    private Protocol protocol;
    private Server server;

    @PostConstruct
    public void init() {
        Properties nettyConfig = zookeeperService.getZookeeperConfig();
        String rpcPort = nettyConfig.getProperty(ProtocolConstants.RPC_SERVER_PORT);
        if (TextUtil.isEmpty(rpcPort)) {
            logger.error("lose " + ProtocolConstants.RPC_SERVER_PORT + " config, cannot start rpc server!");
            port = 0;
        }
        port = Integer.parseInt(rpcPort);
        for (Object key : nettyConfig.keySet()) {
            parameters.put((String) key, (String) nettyConfig.get(key));
        }
    }

    @Override
    public Server getRpcServer() {
        return server;
    }

    @Override
    public Integer getRpcPort() {
        return port;
    }

    @Override
    public void start() {
        try {
            String ip = zookeeperService.getAvailableLocalIp();
            if (TextUtil.isEmpty(ip)) {
                return;
            }
            if (port <= 0) {
                return;
            }
            TransportContext transportContext = new TransportContext(ip, port, parameters, new RpcProtocol.NettyServerCodec(), new RpcTransport(), null, null);
            protocol = new RpcProtocol(transportContext, new RpcRequestHandler(publicServiceHolder.getAllRpcHandler()));
            protocol.bind();
            server = protocol.getServer();
        } catch (TransportException e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("start rpc server failed", e));
        }
    }

    @PreDestroy
    public void stop() {
        logger.info("closing rpc server！");
        if (protocol != null) {
            protocol.destroy();
        }
        logger.info("rpc server closed！");
    }
}
