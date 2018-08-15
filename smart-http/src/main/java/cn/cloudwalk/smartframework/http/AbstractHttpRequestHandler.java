package cn.cloudwalk.smartframework.http;

import cn.cloudwalk.smartframework.common.exception.BaseException;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.JsonUtil;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.common.util.XmlUtil;
import cn.cloudwalk.smartframework.common.util.http.bean.HTTP_CONTENT_TRANSFER_TYPE;
import cn.cloudwalk.smartframework.transport.Channel;
import cn.cloudwalk.smartframework.transport.exchange.support.ExchangeHandlerAdapter;
import cn.cloudwalk.smartframework.transport.support.transport.TransportException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析Http请求
 *
 * @author LIYANHUI
 * @date 2018/1/31
 * @since 1.0.0
 */
public abstract class AbstractHttpRequestHandler extends ExchangeHandlerAdapter {

    /**
     * 上传文件时参数
     */
    private static final String FILE_UPLOAD_PARAM = "FILE_UPLOAD_PARAM";
    private static final String FAVICON_URL = "/favicon.ico";
    private static final String BAD_REQUEST_URl = "/bad-request";
    private static final String EMPTY_URL = "/";
    private static final String URL_PARAMETER_SPLIT = "?";
    /**
     * 空的返回对象
     */
    private static FullHttpResponse EMPTY_RESPONSE = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            Unpooled.EMPTY_BUFFER
    );

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
        EMPTY_RESPONSE.headers().set("Content-Length", Unpooled.EMPTY_BUFFER.readableBytes());
    }

    private static final Logger logger = LogManager.getLogger(AbstractHttpRequestHandler.class);

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
                String uri = request.uri();
                HttpMethod method = request.method();
                HTTP_CONTENT_TRANSFER_TYPE transferType = this.getTransferType(request);
                //浏览器请求过滤掉
                if (FAVICON_URL.equals(uri) || BAD_REQUEST_URl.equals(uri)) {
                    channel.send(EMPTY_RESPONSE);
                    return;
                }

                //不带地址的请求过滤掉
                if (EMPTY_URL.equals(uri)) {
                    this.writeError("400000", "Invalid request address, please confirm if it is a valid service path", channel);
                    return;
                }
                logger.info("received request: url=" + uri + ", method=" + method + ", transferType=" + transferType);
                Map<String, Object> params = this.getRequestParams(transferType, request);
                if (uri.contains(URL_PARAMETER_SPLIT)) {
                    uri = uri.substring(0, uri.indexOf("?"));
                }
                logger.info("request params: " + params);
                Map<String, File> files = (Map<String, File>) params.remove(FILE_UPLOAD_PARAM);
                if (files == null || files.isEmpty()) {
                    this.handle(uri, params, transferType, channel);
                } else {
                    this.handle(uri, files, params, channel);
                }
            } else {
                logger.error("Unable to process request: " + message);
            }
        }finally {
            ReferenceCountUtil.release(message);
        }
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws TransportException {
        String message = "System Error";
        if (exception instanceof BaseException) {
            message = ((BaseException) exception).getDesc().getRespDesc();
        }

        this.writeResponse("{\"respCode\":\"400000\",\"respDesc\":\"" + message + "\"}", channel);
        logger.error(message, exception);
        channel.close();
    }

    /**
     * 转发普通请求
     *
     * @param uri          请求地址
     * @param paramsMap    请求参数
     * @param transferType 请求传输类型
     * @param channel      HTTP连接
     */
    protected abstract void handle(String uri, Map<String, Object> paramsMap, HTTP_CONTENT_TRANSFER_TYPE transferType, Channel channel);

    /**
     * 转发上传文件请求
     *
     * @param uri       请求地址
     * @param paramsMap 请求参数
     * @param files     请求文件
     * @param channel   HTTP连接
     */
    protected abstract void handle(String uri, Map<String, File> files, Map<String, Object> paramsMap, Channel channel);

    private Map<String, Object> getRequestParams(HTTP_CONTENT_TRANSFER_TYPE transferType, HttpRequest request) {
        Map<String, Object> params = TextUtil.parseUrlParams(request.uri());
        if (request.method() == HttpMethod.POST) {
            FullHttpRequest fullReq;
            String content;
            Map<String, Object> mapParams;
            try {
                if (transferType == HTTP_CONTENT_TRANSFER_TYPE.JSON) {
                    fullReq = (FullHttpRequest) request;
                    content = fullReq.content().toString(CharsetUtil.UTF_8);
                    if (TextUtil.isNotEmpty(content)) {
                        mapParams = JsonUtil.json2Map(content);
                        params.putAll(mapParams);
                    }
                } else if (transferType == HTTP_CONTENT_TRANSFER_TYPE.XML) {
                    fullReq = (FullHttpRequest) request;
                    content = fullReq.content().toString(CharsetUtil.UTF_8);
                    if (TextUtil.isNotEmpty(content)) {
                        mapParams = XmlUtil.xml2Map("request", content);
                        params.putAll(mapParams);
                    }
                } else {
                    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
                    List<InterfaceHttpData> postParams = decoder.getBodyHttpDatas();
                    Map<String, File> files = new HashMap<>();
                    for (InterfaceHttpData param : postParams) {
                        if (param.getHttpDataType() != InterfaceHttpData.HttpDataType.FileUpload) {
                            Attribute data = (Attribute) param;
                            try {
                                params.put(data.getName(), data.getValue());
                            } finally {
                                data.release();
                            }
                        }
                        //总共三种类型 最后一种必是FileUpload
                        else {
                            FileUpload data = (FileUpload) param;
                            try {
                                String uploadFileName = getUploadFileName(data);
                                if (data.isCompleted()) {
                                    File dir = new File(System.getProperty("user.dir") + File.separator + "fileUpload" + File.separator);
                                    if (!dir.exists()) {
                                        dir.mkdir();
                                    }
                                    File deistFile = new File(dir, uploadFileName);
                                    try {
                                        data.renameTo(deistFile);
                                        files.put(data.getName(), deistFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } finally {
                                data.release();
                            }
                        }
                    }
                    params.put(FILE_UPLOAD_PARAM, files);
                }
            } catch (Exception exception) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("An error occurred while processing the request parameters", exception));
            }
        }
        return params;
    }

    private String getUploadFileName(FileUpload data) {
        String content = data.toString();
        String temp = content.substring(0, content.indexOf("\n"));
        content = temp.substring(temp.lastIndexOf("=") + 2, temp.lastIndexOf("\""));
        return content;
    }

    private HTTP_CONTENT_TRANSFER_TYPE getTransferType(HttpRequest httpRequest) {
        if (httpRequest.method() == HttpMethod.GET) {
            return HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE;
        } else {
            String contentType = httpRequest.headers().get("Content-Type") + "";
            if (TextUtil.isNotEmpty(contentType) && contentType.contains("application/json")) {
                return HTTP_CONTENT_TRANSFER_TYPE.JSON;
            } else {
                return contentType.contains("/xml") ? HTTP_CONTENT_TRANSFER_TYPE.XML : HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE;
            }
        }
    }

    protected void writeError(String resultCode, String message, Channel channel) throws TransportException {
        try {
            this.writeResponse("{\"respCode\":\"" + resultCode + "\",\"respDesc\":\"" + message + "\"}", channel);
        } finally {
            channel.close();
        }
    }

    protected void writeError(Exception e, Channel channel) throws TransportException {
        this.writeError("400000", "System Error", channel);
    }

    protected void writeResponse(String data, Channel channel) throws TransportException {
        logger.info("Request response result: " + data);
        if (data == null) {
            this.writeError("400000", "request response result is empty", channel);
        } else {
            String contentType;
            if (!data.startsWith("<")) {
                contentType = "application/json";
            } else {
                if (data.startsWith("{") && data.endsWith("}")) {
                    data = JsonUtil.json2Xml("response", data);
                }
                contentType = "application/xml";
            }
            ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
            byteBuf.writeBytes(data.getBytes(CharsetUtil.UTF_8));
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().set("Content-Type", contentType + "; charset=UTF-8");
            response.headers().set("Content-Length", byteBuf.readableBytes());
            channel.send(response);
        }
    }
}
