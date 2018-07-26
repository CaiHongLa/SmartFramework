package cn.cloudwalk.smartframework.rpc.invoke;

import cn.cloudwalk.smartframework.common.IBaseComponent;
import cn.cloudwalk.smartframework.common.protocol.ProtocolIn;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncCallback;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncExecuteResult;
import cn.cloudwalk.smartframework.common.util.http.bean.HTTP_CONTENT_TRANSFER_TYPE;
import cn.cloudwalk.smartframework.common.util.http.bean.ProtocolHttpRequest;
import cn.cloudwalk.smartframework.common.util.http.bean.ProtocolOutResponse;
import org.apache.http.client.config.RequestConfig;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Http服务远程调用
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IRemoteService extends IBaseComponent {

    ProtocolOutResponse syncCall(String serviceUrl);

    ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params);

    ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params, Map<String, File> files);

    ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params, HTTP_CONTENT_TRANSFER_TYPE transferType);

    ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params, Map<String, File> files, boolean responseDirectly);

    ProtocolOutResponse syncCall(String serviceUrl, Map<String, Object> params, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean responseDirectly);

    ProtocolOutResponse syncCall(String serviceUrl, ProtocolIn protocol);

    ProtocolOutResponse syncCall(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType);

    ProtocolOutResponse syncCall(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, RequestConfig requestConfig);

    AsyncExecuteResult asyncCall(String serviceUrl, AsyncCallback callback);

    AsyncExecuteResult asyncCall(String serviceUrl, Map<String, Object> params, AsyncCallback callback);

    AsyncExecuteResult asyncCall(String serviceUrl, Map<String, Object> params, HTTP_CONTENT_TRANSFER_TYPE transferType, AsyncCallback callback);

    AsyncExecuteResult asyncCall(String serviceUrl, ProtocolIn protocol, AsyncCallback callback);

    AsyncExecuteResult asyncCall(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, AsyncCallback callback);

    AsyncExecuteResult asyncCall(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, RequestConfig requestConfig, AsyncCallback callback);

    AsyncExecuteResult asyncCall(ProtocolHttpRequest request, AsyncCallback callback);

    AsyncExecuteResult asyncCall(ProtocolHttpRequest request, RequestConfig requestConfig, AsyncCallback callback);

    AsyncExecuteResult asyncCall(List<ProtocolHttpRequest> request, AsyncCallback callback);

    AsyncExecuteResult asyncCall(List<ProtocolHttpRequest> request, RequestConfig requestConfig, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(String serviceUrl, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(String serviceUrl, Map<String, Object> params, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(String serviceUrl, Map<String, Object> params, HTTP_CONTENT_TRANSFER_TYPE transferType, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(String serviceUrl, ProtocolIn protocol, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(String serviceUrl, ProtocolIn protocol, HTTP_CONTENT_TRANSFER_TYPE transferType, RequestConfig requestConfig, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(ProtocolHttpRequest request, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(ProtocolHttpRequest request, RequestConfig requestConfig, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(List<ProtocolHttpRequest> request, AsyncCallback callback);

    AsyncExecuteResult asyncBroadcast(List<ProtocolHttpRequest> request, RequestConfig requestConfig, AsyncCallback callback);

}
