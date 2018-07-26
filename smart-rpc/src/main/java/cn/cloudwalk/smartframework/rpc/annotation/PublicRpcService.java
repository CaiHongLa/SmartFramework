package cn.cloudwalk.smartframework.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC服务中的Service服务注解。
 * <p>
 * 只能注解到被{@link org.springframework.stereotype.Service}注解的Service并且实现了某个接口。
 * <p>
 * 可以将被注解的类的接口类注册到zookeeper服务。
 * <p>
 * 注册规则：
 * <pre>
 *
 *      根节点/组件id/class/provider-ip:port
 *
 *      根节点：zookeeper.rootPath+rpc
 *      组件id：zookeeper.id
 *      class：接口的完整路径
 *      ip：本机ip
 *      port：Netty Rpc服务端口
 * </pre>
 *
 * @author 李延辉
 * @see PublicHttpService
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface PublicRpcService {

    /**
     * 被注解的Service的接口的类
     *
     * @return 接口的 {@link Class}
     */
    Class<?> value();
}
