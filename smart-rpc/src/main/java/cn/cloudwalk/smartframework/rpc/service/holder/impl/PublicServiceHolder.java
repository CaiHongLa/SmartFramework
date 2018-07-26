package cn.cloudwalk.smartframework.rpc.service.holder.impl;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.rpc.annotation.PublicHttpService;
import cn.cloudwalk.smartframework.rpc.annotation.PublicRpcService;
import cn.cloudwalk.smartframework.rpc.bean.PublicHttpServiceVO;
import cn.cloudwalk.smartframework.rpc.bean.PublicRpcServiceVO;
import cn.cloudwalk.smartframework.rpc.service.holder.IPublicServiceHolder;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PublicServiceHolder
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("publicServiceHolder")
public class PublicServiceHolder extends BaseComponent implements IPublicServiceHolder {

    private List<PublicHttpServiceVO> httpServiceVOS = new ArrayList<>();
    private List<PublicRpcServiceVO> rpcServiceVOS = new ArrayList<>();
    private Map<String, Object> handlerMap = new HashMap<>();

    @Override
    public List<PublicRpcServiceVO> getAllRpcService() {
        return rpcServiceVOS;
    }

    @Override
    public List<PublicHttpServiceVO> getAllHttpService() {
        return httpServiceVOS;
    }

    @Override
    public Map<String, Object> getAllRpcHandler() {
        return handlerMap;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> cls = bean.getClass();
        //*************************HTTP****************************************
        if (cls.isAnnotationPresent(Controller.class) && cls.isAnnotationPresent(PublicHttpService.class)) {
            String[] ctrlMappingName = null;
            if (cls.isAnnotationPresent(RequestMapping.class)) {
                ctrlMappingName = (cls.getAnnotation(RequestMapping.class)).value();
            }
            Method[] methods = bean.getClass().getMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    String[] methodMappingName = (method.getAnnotation(RequestMapping.class)).value();
                    PublicHttpServiceVO httpServiceVO = new PublicHttpServiceVO();
                    httpServiceVO.setControllerMappingName(ctrlMappingName);
                    httpServiceVO.setControllerClassName(cls.getName());
                    httpServiceVO.setMethodMappingName(methodMappingName);
                    httpServiceVO.setMethod(method);
                    this.httpServiceVOS.add(httpServiceVO);
                }
            }
        }
        //*************************Netty Rpc *************************************
        else if (cls.isAnnotationPresent(Service.class) && cls.isAnnotationPresent(PublicRpcService.class)) {
            Class serviceClazz = (cls.getAnnotation(PublicRpcService.class)).value();
            String interfaceName = serviceClazz.getName();
            handlerMap.put(interfaceName, bean);
            PublicRpcServiceVO rpcServiceVO = new PublicRpcServiceVO();
            rpcServiceVO.setClassName(serviceClazz.getName());
            this.rpcServiceVOS.add(rpcServiceVO);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @PreDestroy
    public void release() {
        handlerMap.clear();
        httpServiceVOS.clear();
        rpcServiceVOS.clear();
    }
}
