package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.rpc.annotation.AutowiredService;
import cn.cloudwalk.smartframework.rpc.invoke.proxy.AutoInvokeProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Proxy;

/**
 * 用于加载Service中被AutowiredService注解的字段
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/8/15
 * @since 2.0.10
 */
@Component("autowiredServiceBeanProcessor")
public class AutowiredServiceBeanProcessor implements BeanPostProcessor {

    private static final Logger logger = LogManager.getLogger(AutowiredServiceBeanProcessor.class);

    @Autowired
    @Qualifier("zookeeperService")
    private IZookeeperService zookeeperService;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClazz = bean.getClass();
        ReflectionUtils.doWithLocalFields(beanClazz, field -> {
            boolean isAutoService = field.isAnnotationPresent(AutowiredService.class);
            if (isAutoService) {
                AutowiredService autowiredService = field.getAnnotation(AutowiredService.class);
                String zookeeperId = autowiredService.id();
                if (TextUtil.isEmpty(zookeeperId)) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("can not init field " + field.getName() + " annotated by @AutowiredService for null id!"));
                }
                Class<?> returnType = autowiredService.entity();
                Class<?> interfaceClass = field.getType();
                Object value;
                if(returnType.equals(BaseDomain.class)) {
                    logger.warn("@AutowiredService " + interfaceClass.getName() + " annotated by BaseDomain, so all of IBaseService's methods can not be call!");
                    value = Proxy.newProxyInstance(
                            interfaceClass.getClassLoader(),
                            new Class<?>[]{interfaceClass},
                            new AutoInvokeProxy<>(interfaceClass, zookeeperId, null, zookeeperService));
                } else {
                    value = Proxy.newProxyInstance(
                            interfaceClass.getClassLoader(),
                            new Class<?>[]{interfaceClass},
                            new AutoInvokeProxy<>(interfaceClass, zookeeperId, returnType, zookeeperService));
                }
                ReflectionUtils.makeAccessible(field);
                field.set(bean, value);
            }
        });
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
