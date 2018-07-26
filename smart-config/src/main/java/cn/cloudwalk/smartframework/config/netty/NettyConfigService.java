package cn.cloudwalk.smartframework.config.netty;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.netty.config.INettyConfigService;
import cn.cloudwalk.smartframework.common.util.FileUtil;
import cn.cloudwalk.smartframework.common.util.NetUtil;
import cn.cloudwalk.smartframework.common.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author LIYANHUI
 */
@Component("nettyConfigService")
public class NettyConfigService extends BaseComponent implements INettyConfigService {

    private static final Logger logger = LogManager.getLogger(NettyConfigService.class);
    private static final String NETTY_CFG_FILE_NAME = "netty-cfg.properties";
    private static final String NETTY_IP_PROPERTY = "netty.local.ip";
    private Properties nettyConfig;
    private String localIp;
    private String ipPattern;
    /**
     * Netty服务中不能用127.0.0.1 和 localhost
     */
    private List<String> LOCAL_IP_LIST = Arrays.asList("127.0.0.1", "localhost");

    @PostConstruct
    private void initNettyConfig() {
        if (!FileUtil.isFileExistOnClasspathOrConfigDir(NETTY_CFG_FILE_NAME)) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("在类路径下没有找到配置文件 " + NETTY_CFG_FILE_NAME));
        } else {
            logger.info("开始加载配置文件 " + NETTY_CFG_FILE_NAME);
            this.nettyConfig = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir(NETTY_CFG_FILE_NAME);
            if(!nettyConfig.containsKey(NETTY_IP_PROPERTY)){
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("在配置文件 " + NETTY_CFG_FILE_NAME + " 中没有找到 " + NETTY_IP_PROPERTY + " 配置项！"));
            }
            this.ipPattern = this.nettyConfig.getProperty(NETTY_IP_PROPERTY);
            if (this.LOCAL_IP_LIST.contains(this.ipPattern.trim().toLowerCase())) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("Netty拒绝以 " + this.LOCAL_IP_LIST + " 模式启动，请配置网络IP"));
            }
            logger.info("配置文件 " + NETTY_CFG_FILE_NAME + " 加载完成：" + this.nettyConfig);
        }
    }

    @Override
    public Properties getNettyConfig() {
        return nettyConfig;
    }

    @Override
    public String getLocalIp() {
        if (localIp == null) {
            logger.info("开始根据 " + NETTY_IP_PROPERTY + " 配置项获取可用 ip");
            Map<String, String> ipList = NetUtil.getAvailableIp();
            logger.info("当前主机的可用 ip 列表为：" + ipList);
            Map<String, String> result = NetUtil.getAvailableIp(ipPattern);
            if (result != null && result.size() != 0) {
                if (result.size() > 1) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("找到多个可用 ip（" + result + "），因此无法继续，可能原因为 " + NETTY_IP_PROPERTY + " 正则配置错误（" + ipPattern + "），导致在可用 ip 列表中匹配到了多个，请检查"));
                } else {
                    Map.Entry<String, String> targetIp = result.entrySet().iterator().next();
                    logger.info("已确定可用 ip 为 " + targetIp.getKey() + "（" + targetIp.getValue() + "）");
                    localIp =  targetIp.getKey();
                }
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("没有匹配到可用 ip，当前主机的所有可用 ip 列表为 " + (ipList != null && !ipList.isEmpty() ? ipList.keySet() : "{}") + "，" + "可能原因为 " + NETTY_IP_PROPERTY + " 配置错误（当前配置为 " + ipPattern + "）：" + "1.ip 配置错误，没有包含在当前主机的可用 ip 列表中；" + "2.ip 正则配置错误，因此在所有可用 ip 列表中没有匹配到对象"));
            }
        }
        return localIp;
    }
}
