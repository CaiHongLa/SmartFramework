package cn.cloudwalk.smartframework.common.netty.config;

import cn.cloudwalk.smartframework.common.IBaseComponent;

import java.util.Properties;

/**
 * @author LIYANHUI
 */
public interface INettyConfigService extends IBaseComponent {

    Properties getNettyConfig();

    String getLocalIp();
}
