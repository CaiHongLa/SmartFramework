package cn.cloudwalk.smartframework.rpc.annotation;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rpc自动装载服务注解
 *
 * @author liyanhui@cloudwalk.cn
 * @date 2018/8/15
 * @since 2.0.10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutowiredService {

    /**
     * 服务提供方所在ZookeeperId
     *
     * @return
     */
    String id() default "";

    /**
     * Service使用的实体类
     *
     * @return
     */
    Class<? extends BaseDomain> entity() default BaseDomain.class;

}
