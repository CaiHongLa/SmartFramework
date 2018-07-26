package cn.cloudwalk.smartframework.rpc.service.holder;

import cn.cloudwalk.smartframework.rpc.bean.PublicHttpServiceVO;
import cn.cloudwalk.smartframework.rpc.bean.PublicRpcServiceVO;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;
import java.util.Map;

/**
 * 加载服务时存储服务
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IPublicServiceHolder extends BeanPostProcessor {

    /**
     * 获取所有加载到的Rpc服务
     *
     * @return Rpc服务列表
     */
    List<PublicRpcServiceVO> getAllRpcService();

    /**
     * 获取所有加载到的Http服务
     *
     * @return Http服务列表
     */
    List<PublicHttpServiceVO> getAllHttpService();

    /**
     * 获取Rpc服务的实现bean
     *
     * @return rpc服务的实现列表
     */
    Map<String, Object> getAllRpcHandler();
}
