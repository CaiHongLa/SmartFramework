package cn.cloudwalk.smartframework.transport;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AbstractProtocol
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public abstract class AbstractProtocol implements Protocol {

    protected final Logger logger = LogManager.getLogger(getClass());

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public void destroy() {
    }
}
