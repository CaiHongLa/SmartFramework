package cn.cloudwalk.smartframework.rpc.netty.service;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.netty.config.INettyConfigService;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.rpc.netty.http.RpcHttpProtocol;
import cn.cloudwalk.smartframework.rpc.netty.http.RpcHttpRequestHandler;
import cn.cloudwalk.smartframework.rpc.netty.http.RpcHttpTransport;
import cn.cloudwalk.smartframework.rpc.netty.INettyRpcService;
import cn.cloudwalk.smartframework.rpc.service.holder.IPublicServiceHolder;
import cn.cloudwalk.smartframework.transport.Protocol;
import cn.cloudwalk.smartframework.transport.Server;
import cn.cloudwalk.smartframework.transport.support.ProtocolConstants;
import cn.cloudwalk.smartframework.transport.support.transport.TransportContext;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
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

    @Autowired(required = false)
    @Qualifier("nettyConfigService")
    private INettyConfigService nettyConfigService;

    @Autowired
    @Qualifier("publicServiceHolder")
    private IPublicServiceHolder publicServiceHolder;

    private Map<String, String> parameters = new HashMap<>(50);

    private Integer port;
    private String ip;
    private Protocol protocol;
    private Server server;

    @PostConstruct
    public void init() {
        Properties nettyConfig = getNettyConfigService().getNettyConfig();
        String rpcPort = nettyConfig.getProperty(ProtocolConstants.RPC_HTTP_SERVER_PORT);
        if (TextUtil.isEmpty(rpcPort)) {
            logger.error("缺少" + ProtocolConstants.RPC_HTTP_SERVER_PORT + "配置, 无法启动Rpc服务!");
            port = 0;
        }
        port = Integer.parseInt(rpcPort);
        ip = getNettyConfigService().getLocalIp();
        for (Object key : nettyConfig.keySet()) {
            parameters.put((String) key, (String) nettyConfig.get(key));
        }
    }

    @Override
    public Server getNettyRpcServer() {
        return server;
    }

    @Override
    public Integer getHttpRpcPort() {
        return port;
    }

    @Override
    public void start() {
        try {
            if (TextUtil.isEmpty(ip)) {
                return;
            }
            if (port <= 0) {
                return;
            }
            TransportContext transportContext = new TransportContext(ip, port, parameters, new RpcHttpProtocol.HttpCodec(), new RpcHttpTransport(), null, null);
            protocol = new RpcHttpProtocol(transportContext, new RpcHttpRequestHandler(publicServiceHolder.getAllRpcHandler()));
            protocol.bind();
            server = protocol.getServer();
        } catch (TransportException e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("启动Rpc服务失败", e));
        }
    }

    @PreDestroy
    public void stop() {
        logger.info("开始关闭Rpc服务！");
        if (protocol != null) {
            protocol.destroy();
        }
        logger.info("关闭Rpc服务完成！");
    }

    private INettyConfigService getNettyConfigService() {
        if (nettyConfigService == null) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("INettyConfigService服务不可用，请导入Config组件！"));
        }
        return nettyConfigService;
    }
}
