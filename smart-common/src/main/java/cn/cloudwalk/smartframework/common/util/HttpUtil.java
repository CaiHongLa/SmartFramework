package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.http.ErrorHttpStatusException;
import cn.cloudwalk.smartframework.common.util.http.HttpBaseSupportUtil;
import cn.cloudwalk.smartframework.common.util.http.HttpDownloadListener;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncCallback;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncExecuteResult;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncRpcCallBack;
import cn.cloudwalk.smartframework.common.util.http.async.AsyncRpcExecuteResult;
import cn.cloudwalk.smartframework.common.util.http.bean.HTTP_CONTENT_TRANSFER_TYPE;
import cn.cloudwalk.smartframework.common.util.http.bean.HTTP_METHOD;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpErrorDetail;
import cn.cloudwalk.smartframework.common.util.http.bean.HttpRequest;
import org.apache.commons.io.FileUtils;
import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author LIYANHUI
 */
public class HttpUtil {
    private static Logger logger = LogManager.getLogger(HttpUtil.class);

    public static HttpGet buildHttpGet(String url, Object params, boolean proxy) {
        return buildHttpGet(url, params, null, proxy, null);
    }

    public static HttpGet buildHttpGet(String url, Object params, boolean proxy, RequestConfig requestConfig) {
        return buildHttpGet(url, params, null, proxy, requestConfig);
    }

    public static HttpGet buildHttpGet(String url, Object params, List<? extends Header> headers, boolean proxy, RequestConfig requestConfig) {
        return HttpBaseSupportUtil.buildHttpGet(url, params, headers, proxy, requestConfig);
    }

    public static HttpPost buildHttpPost(String url, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean proxy) {
        return buildHttpPost(url, params, null, transferType, proxy, null);
    }

    public static HttpPost buildHttpPost(String url, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean proxy, RequestConfig requestConfig) {
        return buildHttpPost(url, params, null, transferType, proxy, requestConfig);
    }

    public static HttpPost buildHttpPost(String url, Object params, List<? extends Header> headers, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean proxy, RequestConfig requestConfig) {
        return HttpBaseSupportUtil.buildHttpPost(url, params, headers, transferType, proxy, requestConfig);
    }

    public static String getResponseText(HttpResponse response) {
        return HttpBaseSupportUtil.getResponseText(response);
    }

    public static class Download {
        public Download() {
        }

        public static void request(String url, String savePath) {
            request(url, null, false, null, savePath);
        }

        public static void request(String url, boolean proxy, String savePath) {
            request(url, null, proxy, null, savePath);
        }

        public static void request(String url, Map<String, Object> params, String savePath) {
            request(url, params, false, null, savePath);
        }

        public static void request(String url, Map<String, Object> params, boolean proxy, String savePath) {
            request(url, params, proxy, null, savePath);
        }

        public static void request(String url, Map<String, Object> params, boolean proxy, RequestConfig requestConfig, final String savePath) {
            request(url, params, proxy, requestConfig, is -> FileUtils.copyInputStreamToFile(is, new File(savePath)));
        }

        public static void request(String url, HttpDownloadListener listener) {
            request(url, null, false, null, listener);
        }

        public static void request(String url, Map<String, Object> params, HttpDownloadListener listener) {
            request(url, params, false, null, listener);
        }

        public static void request(String url, Map<String, Object> params, boolean proxy, HttpDownloadListener listener) {
            request(url, params, proxy, null, listener);
        }

        public static void request(String url, Map<String, Object> params, boolean proxy, RequestConfig requestConfig, HttpDownloadListener listener) {
            CloseableHttpClient httpClient = HttpBaseSupportUtil.getOrCreateHttpClient(url, params, proxy);
            HttpGet httpget = HttpUtil.buildHttpGet(url, params, proxy, requestConfig);

            try {
                HttpResponse response = httpClient.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null && listener != null) {
                    listener.onComplete(entity.getContent());
                }

                EntityUtils.consume(entity);
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }
    }

    public static class Multipart {
        public Multipart() {
        }

        public static String request(String url, Map<String, File> files) {
            return request(url, files, null, String.class);
        }

        public static String request(String url, Map<String, File> files, Map<String, Object> params) {
            return request(url, files, params, String.class);
        }

        public static <T> T request(String url, Map<String, File> files, Class<T> type) {
            return request(url, files, null, type);
        }

        public static <T> T request(String url, Map<String, File> files, Map<String, Object> params, Class<T> type) {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpput = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            if (files != null && files.size() > 0) {
                for (Map.Entry<String, File> entry : files.entrySet()) {
                    builder.addBinaryBody(entry.getKey(), entry.getValue());
                }
            }

            if (params != null && params.size() > 0) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    builder.addTextBody(entry.getKey(), (String) entry.getValue());
                }
            }

            httpput.setEntity(builder.build());
            CloseableHttpResponse response = null;

            String json;
            try {
                response = httpclient.execute(httpput);
                json = HttpBaseSupportUtil.getResponseText(response);
                if (type != String.class) {
                    return JsonUtil.json2Object(json, type);
                } else {
                    return (T) json;
                }
            } catch (IOException e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        logger.error("close http stream error！" + e);
                    }
                }
            }
        }
    }

    public static class Sync {
        public Sync() {
        }

        public static String get(String url) {
            return request(url, null, null, HTTP_METHOD.GET, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE, false, null, String.class);
        }

        public static <T> T get(String url, Class<T> responseType) {
            return request(url, null, null, HTTP_METHOD.GET, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE, false, null, responseType);
        }

        public static String get(String url, Object params) {
            return request(url, params, null, HTTP_METHOD.GET, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE, false, null, String.class);
        }

        public static <T> T get(String url, Object params, Class<T> responseType) {
            return request(url, params, null, HTTP_METHOD.GET, HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE, false, null, responseType);
        }

        public static String post(String url) {
            return request(url, null, null, HTTP_METHOD.POST, null, false, null, String.class);
        }

        public static <T> T post(String url, Class<T> responseType) {
            return request(url, null, null, HTTP_METHOD.POST, null, false, null, responseType);
        }

        public static String post(String url, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType) {
            return request(url, params, null, HTTP_METHOD.POST, transferType, false, null, String.class);
        }

        public static <T> T post(String url, Object params, HTTP_CONTENT_TRANSFER_TYPE transferType, Class<T> responseType) {
            return request(url, params, null, HTTP_METHOD.POST, transferType, false, null, responseType);
        }

        public static <T> T request(String url, Object params, HTTP_METHOD httpMethod, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean proxy, Class<T> responseType) {
            return request(url, params, null, httpMethod, transferType, proxy, null, responseType);
        }

        public static <T> T request(String url, Object params, HTTP_METHOD httpMethod, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean proxy, RequestConfig requestConfig, Class<T> responseType) {
            return request(url, params, null, httpMethod, transferType, proxy, null, responseType);
        }

        public static AsyncExecuteResult request(List<HttpRequest> requestList, boolean proxy, RequestConfig requestConfig, AsyncCallback callback) {
            AsyncExecuteResult executeResult = new AsyncExecuteResult();
            int total = requestList.size();
            int index = 1;

            for (HttpRequest metadata : requestList) {
                try {
                    String responseText = request(metadata.getUrl(), metadata.getParams(), metadata.getMethod(), metadata.getTransferType(), proxy, String.class);
                    StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK");
                    callback.onProgressChange((long) (index++), (long) total);
                    callback.onComplete(responseText, metadata, statusLine);
                    executeResult.addResult(metadata.getCode(), new cn.cloudwalk.smartframework.common.util.http.bean.HttpResponse(responseText, statusLine, metadata));
                } catch (Exception e) {
                    callback.onError(e, metadata);
                    executeResult.addError(metadata.getCode(), new HttpErrorDetail(metadata, e));
                }
            }

            return executeResult;
        }

        public static <T> T request(final String url, Object params, List<? extends Header> headers, HTTP_METHOD httpMethod, HTTP_CONTENT_TRANSFER_TYPE transferType, boolean proxy, RequestConfig requestConfig, final Class<T> responseType) {
            ResponseHandler<T> handler = response -> {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new ErrorHttpStatusException(new SystemExceptionDesc("response is null"));
                } else {
                    HttpStatus statusCode = HttpStatus.valueOf(response.getStatusLine().getStatusCode());
                    if (!statusCode.is2xxSuccessful() && !statusCode.is3xxRedirection()) {
                        throw new ErrorHttpStatusException(new SystemExceptionDesc("error HTTP status（statusLine=" + response.getStatusLine() + ", url=" + url + "）"));
                    } else {
                        ContentType contentType = ContentType.getOrDefault(entity);
                        Charset charset = contentType.getCharset();

                        try (InputStreamReader reader = new InputStreamReader(entity.getContent(), charset == null ? Charset.forName("UTF-8") : charset)) {
                            if (responseType == String.class) {
                                StringBuilder content = new StringBuilder();
                                char[] tmp = new char[512];

                                int len;
                                while ((len = reader.read(tmp)) != -1) {
                                    content.append(new String(tmp, 0, len));
                                }
                                return (T) content.toString();
                            }
                            return JsonUtil.json2Object(reader, responseType);
                        }
                    }
                }
            };
            CloseableHttpClient httpclient = HttpBaseSupportUtil.getOrCreateHttpClient(url, params, proxy);
            HttpRequestBase request;
            if (httpMethod == HTTP_METHOD.GET) {
                request = HttpBaseSupportUtil.buildHttpGet(url, params, headers, proxy, requestConfig);
            } else {
                request = HttpBaseSupportUtil.buildHttpPost(url, params, headers, transferType, proxy, requestConfig);
            }

            T resp;

            try {
                resp = httpclient.execute(request, handler, HttpClientContext.create());
            } catch (IOException e) {
                throw new ErrorHttpStatusException(new SystemExceptionDesc(e));
            } finally {
                request.releaseConnection();
            }

            return resp;
        }

        public static void closeHttpClient() {
            HttpBaseSupportUtil.closeHttpClient();
        }
    }

    public static class Async {
        public Async() {
        }

        public static AsyncExecuteResult get(HttpRequest request, AsyncCallback callback) {
            request.setMethod(HTTP_METHOD.GET);
            request.setTransferType(HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE);
            return request(Collections.singletonList(request), false, null, callback);
        }

        public static AsyncExecuteResult get(List<HttpRequest> request, AsyncCallback callback) {

            for (HttpRequest item : request) {
                item.setMethod(HTTP_METHOD.GET);
                item.setTransferType(HTTP_CONTENT_TRANSFER_TYPE.KEY_VALUE);
            }

            return request(request, false, null, callback);
        }

        public static AsyncExecuteResult post(HttpRequest request, AsyncCallback callback) {
            request.setMethod(HTTP_METHOD.POST);
            return request(Collections.singletonList(request), false, null, callback);
        }

        public static AsyncExecuteResult post(List<HttpRequest> request, AsyncCallback callback) {

            for (HttpRequest aRequest : request) {
                aRequest.setMethod(HTTP_METHOD.POST);
            }

            return request(request, false, null, callback);
        }

        public static AsyncRpcExecuteResult postRpc(HttpRequest request, AsyncRpcCallBack callback) {
            request.setMethod(HTTP_METHOD.POST);
            return requestRpc(Collections.singletonList(request), false, null, callback);
        }

        public static AsyncExecuteResult request(List<HttpRequest> requestList, boolean proxy, AsyncCallback callback) {
            return request(requestList, proxy, null, callback);
        }

        public static AsyncExecuteResult request(List<HttpRequest> requestList, boolean proxy, RequestConfig requestConfig, final AsyncCallback callback) {
            HttpBaseSupportUtil.checkRequestCodeConfig(requestList);
            callback.onStart();
            final AsyncExecuteResult executeResult = new AsyncExecuteResult();
            final int total = requestList.size();
            final CountDownLatch countDownLatch = new CountDownLatch(requestList.size());

            for (HttpRequest metadata : requestList) {
                final Object request;
                if (metadata.getMethod() == HTTP_METHOD.GET) {
                    request = HttpBaseSupportUtil.buildHttpGet(metadata.getUrl(), metadata.getParams(), metadata.getHeaders(), proxy, requestConfig);
                } else {
                    request = HttpBaseSupportUtil.buildHttpPost(metadata.getUrl(), metadata.getParams(), metadata.getHeaders(), metadata.getTransferType(), proxy, requestConfig);
                }

                CloseableHttpAsyncClient currClient = HttpBaseSupportUtil.getOrCreateAsyncHttpClient(metadata.getUrl(), metadata.getParams(), proxy);
                currClient.execute((HttpUriRequest) request, new FutureCallback<HttpResponse>() {
                    @Override
                    public void completed(HttpResponse response) {
                        try {
                            ((HttpRequestBase) request).releaseConnection();
                            callback.onProgressChange((long) total - countDownLatch.getCount() - 1L, (long) total);
                            HttpStatus statusCode = HttpStatus.valueOf(response.getStatusLine().getStatusCode());
                            String responseText = HttpBaseSupportUtil.getResponseText(response);
                            metadata.setResponse(responseText);
                            if (!statusCode.is2xxSuccessful() && !statusCode.is3xxRedirection()) {
                                ErrorHttpStatusException exception = new ErrorHttpStatusException(new SystemExceptionDesc("error http status （statusCode=" + statusCode + ", url=" + metadata.getUrl() + "）"));
                                callback.onError(exception, metadata);
                                executeResult.addError(metadata.getCode(), new HttpErrorDetail(metadata, exception));
                            } else {
                                callback.onComplete(responseText, metadata, response.getStatusLine());
                                executeResult.addResult(metadata.getCode(), new cn.cloudwalk.smartframework.common.util.http.bean.HttpResponse(responseText, response.getStatusLine(), metadata));
                            }
                        } catch (Exception e) {
                            logger.error("cached http client callback error " + e);
                        } finally {
                            countDownLatch.countDown();
                        }

                    }

                    @Override
                    public void failed(Exception ex) {
                        try {
                            ((HttpRequestBase) request).releaseConnection();
                            callback.onProgressChange(total - countDownLatch.getCount() - 1L, (long) total);
                            callback.onError(ex, metadata);
                            executeResult.addError(metadata.getCode(), new HttpErrorDetail(metadata, ex));
                        } catch (Exception e) {
                            logger.error("cached http client callback error " + e);
                        } finally {
                            countDownLatch.countDown();
                        }

                    }

                    @Override
                    public void cancelled() {
                        try {
                            ((HttpRequestBase) request).releaseConnection();
                            callback.onProgressChange(total - countDownLatch.getCount() - 1L, (long) total);
                            callback.onCancel(metadata);
                        } catch (Exception e) {
                            logger.error("cached http client callback error " + e);
                        } finally {
                            countDownLatch.countDown();
                        }

                    }
                });
            }

            try {
                countDownLatch.await();
                callback.onEnd(requestList);
                return executeResult;
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }

        public static AsyncRpcExecuteResult requestRpc(List<HttpRequest> requestList, boolean proxy, RequestConfig requestConfig, final AsyncRpcCallBack callback) {
            HttpBaseSupportUtil.checkRequestCodeConfig(requestList);
            final AsyncRpcExecuteResult executeRpcResult = new AsyncRpcExecuteResult();
            final CountDownLatch countDownLatch = new CountDownLatch(requestList.size());

            for (HttpRequest metadata : requestList) {
                final HttpRequestBase request;
                if (metadata.getMethod() == HTTP_METHOD.GET) {
                    request = HttpBaseSupportUtil.buildHttpGet(metadata.getUrl(), metadata.getParams(), metadata.getHeaders(), proxy, requestConfig);
                } else {
                    request = HttpBaseSupportUtil.buildHttpPost(metadata.getUrl(), metadata.getParams(), metadata.getHeaders(), metadata.getTransferType(), proxy, requestConfig);
                }

                CloseableHttpAsyncClient currClient = HttpBaseSupportUtil.getOrCreateAsyncHttpClient(metadata.getUrl(), metadata.getParams(), proxy);
                currClient.execute(request, new FutureCallback<HttpResponse>() {
                    @Override
                    public void completed(HttpResponse response) {
                        try {
                            request.releaseConnection();
                            HttpStatus statusCode = HttpStatus.valueOf(response.getStatusLine().getStatusCode());
                            byte[] data = HttpBaseSupportUtil.getResponseByte(response);
                            if (!statusCode.is2xxSuccessful() && !statusCode.is3xxRedirection()) {
                                ErrorHttpStatusException exception = new ErrorHttpStatusException(new SystemExceptionDesc("error http status （statusCode=" + statusCode + ", url=" + metadata.getUrl() + "）"));
                                callback.onError(exception, metadata);
                                executeRpcResult.addError(metadata.getCode(), new HttpErrorDetail(metadata, exception));
                            } else {
                                callback.onComplete(data, metadata, response.getStatusLine());
                                executeRpcResult.addResult(metadata.getCode(), data);
                            }
                        } catch (Exception e) {
                            logger.error("cached http client callback error " + e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }

                    @Override
                    public void failed(Exception ex) {
                        try {
                            request.releaseConnection();
                            callback.onError(ex, metadata);
                            executeRpcResult.addError(metadata.getCode(), new HttpErrorDetail(metadata, ex));
                        } catch (Exception e) {
                            logger.error("cached http client callback error " + e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }

                    @Override
                    public void cancelled() {
                        try {
                            request.releaseConnection();
                        } catch (Exception e) {
                            logger.error("cached http client callback error " + e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                });
            }

            try {
                countDownLatch.await();
                return executeRpcResult;
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }

        public static void closeAsyncHttpClient() {
            HttpBaseSupportUtil.closeAsyncHttpClient();
        }
    }
}
