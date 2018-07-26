package cn.cloudwalk.smartframework.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC服务中的Http服务注解。
 * <p>
 * 只能注解到被{@link org.springframework.stereotype.Controller} 注解的Controller层。
 * <p>
 * 可以将Controller层所有注解了{@link org.springframework.web.bind.annotation.RequestMapping}的接口注册到zookeeper服务。
 * <p>
 * 注册规则：
 * <pre>
 *
 *      根节点/组件id/url/provider-ip:port
 *
 *      根节点：zookeeper.rootPath+http
 *      组件id：zookeeper.id
 *      url：RequestMapping.value
 *      ip：本机ip
 *      port：web容器启动端口
 * </pre>
 *
 * @author 李延辉
 * @see PublicRpcService
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface PublicHttpService {
}
