package cn.cloudwalk.smartframework.rpc.client;

import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcResponse;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.config.SpringContextHolder;
import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.exchange.support.ExchangeHandlerAdapter;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * 解析Rpc请求
 *
 * @author LIYANHUI
 * @since 2.0.10
 */
public class RpcRequestHandler extends ExchangeHandlerAdapter {

    private final Map<String, Object> handlers;

    public RpcRequestHandler(Map<String, Object> handlers) {
        this.handlers = handlers;
    }

    private static final Logger logger = LogManager.getLogger(RpcRequestHandler.class);

    @Override
    public void connected(Channel channel) throws TransportException {
        super.connected(channel);
    }

    @Override
    public void disconnected(Channel channel) throws TransportException {
        super.disconnected(channel);
    }

    @Override
    public void send(Channel channel, Object message) throws TransportException {
        super.send(channel, message);
    }

    @Override
    public void received(Channel channel, Object message) throws TransportException {
        try {
            if (message instanceof NettyRpcRequest) {
                NettyRpcRequest request = (NettyRpcRequest) message;
                logger.info("received request，class：" + request.getClassName() + "，method：" + request.getMethodName() + "，id：" + request.getRequestId() + "，one way：" + request.getOneWay());
                NettyRpcResponse response = new NettyRpcResponse();
                response.setRequestId(request.getRequestId());

                //单向请求 先返回一个结果 然后处理请求
                if (request.getOneWay()) {
                    writeResponse(response, channel);
                }

                logger.info("request params：" + Arrays.toString(request.getParameters()));
                try {
                    Object result = handle(request.getClassName(), request.getMethodName(), request);
                    response.setResult(result);
                } catch (Exception e) {
                    //对于反射到bean的异常 认为是业务处理异常 需要将异常写回到调用方后再将异常在提供方抛出
                    logger.error("Handling Rpc request exceptions！", e);
                    if(e instanceof FrameworkInternalSystemException) {
                        response.setError(e);
                    } else {
                        String errorMessage = e.getMessage();
                        response.setError(new FrameworkInternalSystemException(new SystemExceptionDesc(e, errorMessage)));
                    }
                    //不是单向请求的 在这里把处理结果写回去
                    if (!request.getOneWay()) {
                        writeResponse(response, channel);
                    }
                    throw e;
                }

                //不是单向请求的 在这里把处理结果写回去
                if (!request.getOneWay()) {
                    writeResponse(response, channel);
                }
            } else {
                logger.error("Unable to process request: " + message);
            }
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    /**
     * 执行Rpc请求
     *
     * @param request 请求
     * @return 执行结果
     */
    private Object handle(String className, String methodName, NettyRpcRequest request) {
        Object serviceBean = handlers.get(className);
        if (null == serviceBean) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(className + " does not have an available implementation class provider, possibly because the interface does not implement a class or the implementation class is not annotated as PublicRpcService"));
        }
        Class<?> serviceClass = serviceBean.getClass();
        //使用Spring代理的bean触发Aop响应
        Object targetBean = SpringContextHolder.getStaticApplicationContext().getBean(serviceClass);
        Class<?>[] parameterTypes = request.getParameterTypes();
        Method method = ReflectionUtils.findMethod(serviceClass, methodName, parameterTypes);
        if(null == method){
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(className + " is not available. Please check if the name of the method is correct."));
        }
        return ReflectionUtils.invokeMethod(method, targetBean, request.getParameters());
    }


    @Override
    public void caught(Channel channel, Throwable exception) throws TransportException{
        logger.error(exception);
        if(exception instanceof TransportException) {
            channel.close();
            return;
        }
        throw new TransportException(channel, exception);
    }

    private void writeResponse(NettyRpcResponse response, Channel channel) throws TransportException {
        logger.info("response: " + response);
        channel.send(response);
    }
}
