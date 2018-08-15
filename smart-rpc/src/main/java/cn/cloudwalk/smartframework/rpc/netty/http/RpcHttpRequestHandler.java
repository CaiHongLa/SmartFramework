package cn.cloudwalk.smartframework.rpc.netty.http;

import cn.cloudwalk.smartframework.common.distributed.bean.NettyRpcRequest;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.JsonUtil;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.config.SpringContextHolder;
import cn.cloudwalk.smartframework.rpc.netty.codec.SerializationUtil;
import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.exchange.support.ExchangeHandlerAdapter;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import com.google.common.primitives.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析Http请求
 *
 * @author LIYANHUI
 * @since 2.0.0
 */
public class RpcHttpRequestHandler extends ExchangeHandlerAdapter {

    private final Map<String, Object> handlers;
    /**
     * 空的返回对象
     */
    private static FullHttpResponse EMPTY_RESPONSE = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            Unpooled.EMPTY_BUFFER
    );

    static {
        EMPTY_RESPONSE.headers().set("Content-Length", Unpooled.EMPTY_BUFFER.readableBytes());
    }

    public RpcHttpRequestHandler(Map<String, Object> handlers) {
        this.handlers = handlers;
    }

    private static final Logger logger = LogManager.getLogger(RpcHttpRequestHandler.class);

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
            if (message instanceof HttpRequest) {
                HttpRequest request = (HttpRequest) message;
                String url = request.uri();
                Map<String, Object> params = TextUtil.parseUrlParams(url);
                final String className = url.substring(1, url.indexOf("?")).replace("/", ".");
                final String requestId = (String) params.get("requestId");
                final String methodName = (String) params.get("methodName");
                final boolean oneWay = Boolean.valueOf((String) params.get("oneWay"));
                logger.info("received request，class：" + className + "，method：" + methodName + "，id：" + requestId + "，one way：" + oneWay);
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("requestId", requestId);

                //单向请求 先返回一个结果 然后处理请求
                if (oneWay) {
                    writeResponse(JsonUtil.object2Json(resultMap), channel);
                }

                NettyRpcRequest nettyRpcRequest;
                try {
                    nettyRpcRequest = this.getRequestParams(request);
                } catch (Exception e) {
                    String errorMessage = "Handling Rpc request parameter exception";
                    logger.error(errorMessage, e);
                    resultMap.put("error", new FrameworkInternalSystemException(new SystemExceptionDesc(e, errorMessage)));
                    writeResponse(JsonUtil.object2Json(resultMap), channel);
                    return;
                }
                logger.info("request params：" + nettyRpcRequest);
                try {
                    Object result = handle(className, methodName, nettyRpcRequest);
                    resultMap.put("result", result);
                } catch (Exception e) {
                    //对于反射到bean的异常 认为是业务处理异常 需要将异常写回到调用方后再将异常在提供方抛出
                    logger.error("Handling Rpc request exceptions！", e);
                    if(e instanceof FrameworkInternalSystemException) {
                        resultMap.put("error", e);
                    } else {
                        String errorMessage = e.getMessage();
                        resultMap.put("error", new FrameworkInternalSystemException(new SystemExceptionDesc(e, errorMessage)));
                    }
                    //不是单向请求的 在这里把处理结果写回去
                    if (!oneWay) {
                        writeResponse(JsonUtil.object2Json(resultMap), channel);
                    }
                    throw e;
                }

                //不是单向请求的 在这里把处理结果写回去
                if (!oneWay) {
                    writeResponse(JsonUtil.object2Json(resultMap), channel);
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

    @SuppressWarnings("unchecked")
    private NettyRpcRequest getRequestParams(HttpRequest request) {
        Map<String, Object> params = new HashMap<>();
        FullHttpRequest fullReq;
        String content;
        Map<String, Object> mapParams;
        fullReq = (FullHttpRequest) request;
        content = fullReq.content().toString(CharsetUtil.UTF_8);
        if (TextUtil.isNotEmpty(content)) {
            mapParams = JsonUtil.json2Map(content);
            params.putAll(mapParams);
        }
        List<Byte> data = (List) params.get("data");
        byte[] byteData = Bytes.toArray(data);
        return SerializationUtil.deserialize(byteData, NettyRpcRequest.class);
    }

    private void writeResponse(String data, Channel channel) throws TransportException {
        logger.info("response: " + data);
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        byteBuf.writeBytes(data.getBytes(CharsetUtil.UTF_8));
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().set("Content-Type", "application/json;charset=UTF-8");
        response.headers().set("Content-Length", byteBuf.readableBytes());
        channel.send(response);
    }
}
