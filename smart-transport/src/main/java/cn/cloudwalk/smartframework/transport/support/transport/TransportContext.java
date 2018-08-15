package cn.cloudwalk.smartframework.transport.support.transport;

import cn.cloudwalk.smartframework.transport.Codec;
import cn.cloudwalk.smartframework.transport.Dispatcher;
import cn.cloudwalk.smartframework.transport.ThreadPool;
import cn.cloudwalk.smartframework.transport.Transport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 传输协议上下文
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public final class TransportContext implements Serializable {

    private static final Logger logger = LogManager.getLogger(TransportContext.class);

    private String host;
    private int port;
    private Map<String, String> parameters;
    private Codec codec;
    private Transport transport;
    private Dispatcher dispatcher;
    private ThreadPool threadPool;

    public TransportContext(String host, int port, Codec codec, Transport transport, ThreadPool threadPool, Dispatcher dispatcher) {
        this(host, port, null, codec, transport, threadPool, dispatcher);
    }

    public TransportContext(String host, int port, Map<String, String> parameters, Codec codec, Transport transport, ThreadPool threadPool, Dispatcher dispatcher) {
        this.host = host;
        this.port = port;
        this.codec = codec;
        this.transport = transport;
        this.dispatcher = dispatcher;
        this.threadPool = threadPool;

        if (parameters == null) {
            parameters = new HashMap<>();
        } else {
            parameters = new HashMap<>(parameters);
        }
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public Transport getTransport() {
        return transport;
    }

    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }

    public int getParameter(String key, int defaultValue) {
        String value = getParameter(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("key: " + key + ",value: " + value + " not numbers! Default values will be used：" + defaultValue);
            return defaultValue;
        }
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public TransportContext addParameter(String key, String value) {
        if (key == null || key.length() == 0
                || value == null || value.length() == 0) {
            return this;
        }
        if (parameters.containsKey(key)) {
            String oldValue = getParameter(key);
            if (value.equals(oldValue)) {
                logger.warn("key: " + key + " already exist，value: " + oldValue);
                return this;
            } else {
                logger.warn("key: " + key + " already exist，new value will be used: " + value + " , old value：" + oldValue);
            }
        }
        Map<String, String> map = new HashMap<>(parameters);
        map.put(key, value);
        parameters = Collections.unmodifiableMap(map);
        return new TransportContext(host, port, parameters, codec, transport, threadPool, dispatcher);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Codec getCodec() {
        return codec;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }
}
