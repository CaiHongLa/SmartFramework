package cn.cloudwalk.smartframework.rpc.invoke.service;


import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IRemoteService;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;
import cn.cloudwalk.smartframework.common.distributed.provider.HttpServiceProvider;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.protocol.ProtocolIn;
import cn.cloudwalk.smartframework.common.util.HttpUtil;
import cn.cloudwalk.smartframework.common.util.JsonUtil;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncCallback;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncExecuteResult;
import cn.cloudwalk.smartframework.common.util.http.bean.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * AutowiredService
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("remoteService")
public class RemoteService extends BaseComponent implements IRemoteService {
    private static final Logger logger = LogManager.getLogger(RemoteService.class);

    @Autowired
    @Qualifier("zookeeperService")
    private IZookeeperService zookeeperService;

    @Override
    public ProtocolOutResponse syncCall(String serviceUrl) {
        return this.processSync(serviceUrl, Collections.emptyMap(), HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE, null);
    }

    @Override
    public ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params) {
        return this.processSync(serviceUrl, params, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE, null);
    }

    @Override
    public ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params, Map<String, File> files) {
        return this.processSyncMultipart(serviceUrl, params, files);
    }

    @Override
    public ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params, HTTP_CONTENT_TRANSFER_TYPE transferType) {
        return this.processSync(serviceUrl, params, transferType, null);
    }

    @Override
    public ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params, Map<String, File> files, boolean responseDirectly) {
        return processSyncMultipart(serviceUrl, files, params, responseDirectly);
    }

    @Override
    public ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean responseDirectly) {
        return this.processSync(serviceUrl, params, transferType, null, responseDirectly);
    }

    @Override
    public ProtocolOutResponse syncCall(String serviceUrl, ProtocolIn protocol) {
        return this.processSync(serviceUrl, protocol, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE, null);
    }

    @Override
    public ProtocolOutResponse syncCall(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType) {
        return this.processSync(serviceUrl, protocol, transferType, null);
    }

    @Override
    public ProtocolOutResponse syncCall(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, RequestConfig requestConfig) {
        return this.processSync(serviceUrl, protocol, transferType, requestConfig);
    }

    @Override
    public AsyncExecuteResult asyncCall(String serviceUrl, AsyncCallback callback) {
        return this.processAsync(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE)), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncCall(String serviceUrl, Map<String, Object> params, AsyncCallback asyncCallback) {
        return this.processAsync(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, params, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE)), null, asyncCallback);
    }

    @Override
    public AsyncExecuteResult asyncCall(String serviceUrl, Map<String, Object> params, HTTP_CONTENT_TRANSFER_TYPE transferType, AsyncCallback callback) {
        return this.processAsync(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, params, transferType)), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncCall(String serviceUrl, ProtocolIn protocol, AsyncCallback callback) {
        return this.processAsync(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, protocol, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE)), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncCall(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, AsyncCallback callback) {
        return this.processAsync(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, protocol, transferType)), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncCall(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, RequestConfig requestConfig, AsyncCallback callback) {
        return this.processAsync(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, protocol, transferType)), requestConfig, callback);
    }

    @Override
    public AsyncExecuteResult asyncCall(ProtocolHttpRequest request, AsyncCallback callback) {
        return this.processAsync(Collections.singletonList(request), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncCall(ProtocolHttpRequest request, RequestConfig requestConfig, AsyncCallback callback) {
        return this.processAsync(Collections.singletonList(request), requestConfig, callback);
    }

    @Override
    public AsyncExecuteResult asyncCall(List<ProtocolHttpRequest> request, AsyncCallback callback) {
        return this.processAsync(request, null, callback);
    }

    @Override
    public AsyncExecuteResult asyncCall(List<ProtocolHttpRequest> request, RequestConfig requestConfig, AsyncCallback callback) {
        return this.processAsync(request, requestConfig, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(String serviceUrl, AsyncCallback callback) {
        return this.processAsyncBroadcast(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE)), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(String serviceUrl, Map<String, Object> params, AsyncCallback callback) {
        return this.processAsyncBroadcast(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, params, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE)), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(String serviceUrl, Map<String, Object> params, HTTP_CONTENT_TRANSFER_TYPE transferType, AsyncCallback callback) {
        return this.processAsyncBroadcast(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, params, transferType)), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(String serviceUrl, ProtocolIn protocol, AsyncCallback callback) {
        return this.processAsyncBroadcast(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, protocol, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE)), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, AsyncCallback callback) {
        return this.processAsyncBroadcast(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, protocol, transferType)), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, RequestConfig requestConfig, AsyncCallback callback) {
        return this.processAsyncBroadcast(Collections.singletonList(new ProtocolHttpRequest(serviceUrl, protocol, transferType)), requestConfig, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(ProtocolHttpRequest request, AsyncCallback callback) {
        return this.processAsyncBroadcast(Collections.singletonList(request), null, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(ProtocolHttpRequest request, RequestConfig requestConfig, AsyncCallback callback) {
        return this.processAsyncBroadcast(Collections.singletonList(request), requestConfig, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(List<ProtocolHttpRequest> request, AsyncCallback callback) {
        return this.processAsyncBroadcast(request, null, callback);
    }

    @Override
    public AsyncExecuteResult asyncBroadcast(List<ProtocolHttpRequest> request, RequestConfig requestConfig, AsyncCallback callback) {
        return this.processAsyncBroadcast(request, requestConfig, callback);
    }

    private ProtocolOutResponse processSync(String serviceUrl, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType, RequestConfig requestConfig) {
        return this.processSync(serviceUrl, params, transferType, requestConfig, false);
    }

    private ProtocolOutResponse processSync(String serviceUrl, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType, RequestConfig requestConfig, boolean responseDirectly) {
        if (!serviceUrl.contains(":")) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("Invalid url：" + serviceUrl + "，for example 'zookeeperId:serviceUrl'"));
        } else {
            String[] part = serviceUrl.split(":");
            String zookeeperId = part[0];
            String api = part[1];
            if (!api.startsWith("/")) {
                api = "/" + api;
            }

            IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
            ProtocolOutResponse response = null;
            if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
                String zookeeperPath = zookeeperService.getHttpServicePath() + "/" + zookeeperId + "" + api;
                DistributedServiceProvider node = this.zookeeperService.getBestServiceProvider(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.HTTP);
                if (node instanceof HttpServiceProvider) {
                    String fullUrl = ((HttpServiceProvider) node).buildUrl();
                    String responseText = HttpUtil.Sync.request(fullUrl, params, HTTP_METHOD.POST, transferType, false, requestConfig, String.class);
                    ProtocolOutResponseMetadata metadata = new ProtocolOutResponseMetadata();
                    metadata.setUrl(fullUrl);
                    if (responseDirectly) {
                        response = new ProtocolOutResponse(responseText);
                    } else {
                        response = JsonUtil.json2Object(responseText, ProtocolOutResponse.class);
                    }
                    response.setMetadata(metadata);
                } else {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("HTTP model request error！"));
                }
            } else if (runningMode == IZookeeperService.RUNNING_MODE.STANDALONE) {
                ServletContext context = getServletContext();
                if(null != context) {
                    String fullUrl = "http://127.0.0.1:" + this.zookeeperService.getAvailableLocalPort() + "/" + context.getContextPath().replaceAll("/", "") + api;
                    String responseText = HttpUtil.Sync.request(fullUrl, params, HTTP_METHOD.POST, transferType, false, requestConfig, String.class);
                    ProtocolOutResponseMetadata metadata = new ProtocolOutResponseMetadata();
                    metadata.setUrl(fullUrl);
                    if (responseDirectly) {
                        response = new ProtocolOutResponse(responseText);
                    } else {
                        response = JsonUtil.json2Object(responseText, ProtocolOutResponse.class);
                    }

                    response.setMetadata(metadata);
                } else {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("Servlet is null！"));
                }
            }
            return response;
        }
    }

    private AsyncExecuteResult processAsync(List<ProtocolHttpRequest> request, RequestConfig requestConfig, AsyncCallback asyncCallback) {
        if (request != null && request.size() > 0) {
            List<HttpRequest> requestList = new ArrayList<>();
            for (ProtocolHttpRequest protocolRequest : request) {
                String serviceUrl = protocolRequest.getServiceUrl();
                if (!serviceUrl.contains(":")) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("Invalid url：" + serviceUrl + "，for example 'zookeeperId:serviceUrl'"));
                }

                if (protocolRequest.getTransferType() == null) {
                    protocolRequest.setTransferType(HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE);
                    logger.info("request " + protocolRequest.getServiceUrl() + " not set HTTP_TRANSFER_TYPE ，use KEY_VALUE");
                }

                String[] part = serviceUrl.split(":");
                String zookeeperId = part[0];
                String api = part[1];
                if (!api.startsWith("/")) {
                    api = "/" + api;
                }

                IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
                if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
                    String zookeeperPath = zookeeperService.getHttpServicePath() + "/" + zookeeperId + "" + api;
                    DistributedServiceProvider node = this.zookeeperService.getBestServiceProvider(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.HTTP);
                    if (node instanceof HttpServiceProvider) {
                        HttpServiceProvider httpServiceProvider = (HttpServiceProvider) node;
                        HttpRequest req = new HttpRequest(protocolRequest.getCode(), httpServiceProvider.buildUrl(), protocolRequest.getParams(), protocolRequest.getTransferType());
                        requestList.add(req);
                    } else {
                        throw new FrameworkInternalSystemException(new SystemExceptionDesc("HTTP model request error！"));
                    }
                } else if (runningMode == IZookeeperService.RUNNING_MODE.STANDALONE) {
                    ServletContext context = getServletContext();
                    if(null != context) {
                        String url = "http://127.0.0.1:" + this.zookeeperService.getAvailableLocalPort() + "/" + context.getContextPath().replaceAll("/", "") + api;
                        HttpRequest req = new HttpRequest(protocolRequest.getCode(), url, protocolRequest.getParams(), protocolRequest.getTransferType());
                        requestList.add(req);
                    } else {
                        throw new FrameworkInternalSystemException(new SystemExceptionDesc("Servlet is null！"));
                    }
                }
            }
            logger.info("ready to send " + requestList.size() + " requests：" + requestList);
            return HttpUtil.Sync.request(requestList, false, requestConfig, asyncCallback);
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("empty request list"));
        }
    }

    private ProtocolOutResponse processSyncMultipart(String serviceUrl, Map<String, Object> params, Map<String, File> files) {
        return processSyncMultipart(serviceUrl, files, params, false);
    }

    private ProtocolOutResponse processSyncMultipart(String serviceUrl, Map<String, File> files, Map<String, Object> params, boolean responseDirectly) {
        if (!serviceUrl.contains(":")) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("Invalid url：" + serviceUrl + "，for example 'zookeeperId:serviceUrl'"));
        } else {
            String[] part = serviceUrl.split(":");
            String zookeeperId = part[0];
            String api = part[1];
            if (!api.startsWith("/")) {
                api = "/" + api;
            }

            IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
            ProtocolOutResponse response = null;

            if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
                String zookeeperPath = zookeeperService.getHttpServicePath() + "/" + zookeeperId + "" + api;
                DistributedServiceProvider node = this.zookeeperService.getBestServiceProvider(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.HTTP);
                if (node instanceof HttpServiceProvider) {
                    String fullUrl = ((HttpServiceProvider) node).buildUrl();
                    String responseText = HttpUtil.Multipart.request(fullUrl, files, params);
                    ProtocolOutResponseMetadata metadata = new ProtocolOutResponseMetadata();
                    metadata.setUrl(fullUrl);
                    if (responseDirectly) {
                        response = new ProtocolOutResponse(responseText);
                    } else {
                        response = JsonUtil.json2Object(responseText, ProtocolOutResponse.class);
                    }
                    response.setMetadata(metadata);
                } else {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("HTTP model request error！"));
                }
            } else if (runningMode == IZookeeperService.RUNNING_MODE.STANDALONE) {
                ServletContext context = getServletContext();
                if(null != context) {
                    String fullUrl = "http://127.0.0.1:" + this.zookeeperService.getAvailableLocalPort() + "/" + context.getContextPath().replaceAll("/", "") + api;
                    String responseText = HttpUtil.Multipart.request(fullUrl, files, params);
                    ProtocolOutResponseMetadata metadata = new ProtocolOutResponseMetadata();
                    metadata.setUrl(fullUrl);
                    if (responseDirectly) {
                        response = new ProtocolOutResponse(responseText);
                    } else {
                        response = JsonUtil.json2Object(responseText, ProtocolOutResponse.class);
                    }

                    response.setMetadata(metadata);
                } else {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("Servlet is null！"));
                }
            }
            return response;
        }
    }

    private AsyncExecuteResult processAsyncBroadcast(List<ProtocolHttpRequest> request, RequestConfig requestConfig, AsyncCallback asyncCallback) {
        if (request != null && request.size() > 0) {
            List<HttpRequest> requestList = new ArrayList<>();
            for (ProtocolHttpRequest protocolRequest : request) {
                String serviceUrl = protocolRequest.getServiceUrl();
                if (!serviceUrl.contains(":")) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("Invalid url：" + serviceUrl + "，for example 'zookeeperId:serviceUrl'"));
                }

                if (protocolRequest.getTransferType() == null) {
                    protocolRequest.setTransferType(HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE);
                    logger.info("request " + protocolRequest.getServiceUrl() + " not set HTTP_TRANSFER_TYPE ，use KEY_VALUE");
                }

                String[] part = serviceUrl.split(":");
                String zookeeperId = part[0];
                String api = part[1];
                if (!api.startsWith("/")) {
                    api = "/" + api;
                }

                IZookeeperService.RUNNING_MODE runningMode = this.zookeeperService.getRunningMode();
                if (runningMode == IZookeeperService.RUNNING_MODE.DISTRIBUTED) {
                    String zookeeperPath = zookeeperService.getHttpServicePath() + "/" + zookeeperId + "" + api;
                    List<DistributedServiceProvider> nodes = this.zookeeperService.getAvailableServiceList(zookeeperPath, IZookeeperService.REMOTE_SERVICE_TYPE.HTTP);
                    if(nodes != null && !nodes.isEmpty()){
                        for(DistributedServiceProvider node : nodes){
                            if (node instanceof HttpServiceProvider) {
                                HttpServiceProvider httpServiceProvider = (HttpServiceProvider) node;
                                HttpRequest req = new HttpRequest(protocolRequest.getCode(), httpServiceProvider.buildUrl(), protocolRequest.getParams(), protocolRequest.getTransferType());
                                requestList.add(req);
                            } else {
                                throw new FrameworkInternalSystemException(new SystemExceptionDesc("HTTP model request error！"));
                            }
                        }
                    } else {
                        logger.error(zookeeperPath + " no available service provider, make sure that the service still has a running provider instance, or that the service address is correct");
                        throw new FrameworkInternalSystemException(new SystemExceptionDesc(zookeeperPath + "no available service provider, make sure that the service still has a running provider instance, or that the service address is correct"));
                    }
                } else if (runningMode == IZookeeperService.RUNNING_MODE.STANDALONE) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("STANDALONE model not support broadcast！"));
                }
            }
            logger.info("ready to send " + requestList.size() + " requests：" + requestList);
            return HttpUtil.Sync.request(requestList, false, requestConfig, asyncCallback);
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("empty request list"));
        }
    }
}
