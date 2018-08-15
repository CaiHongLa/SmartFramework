package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

/**
 * @author LIYANHUI
 */
public class ServerUtil {
    private static Logger logger = LogManager.getLogger(ServerUtil.class);

    public static Integer getTomcatPortFromXmlConfig(ServletContext servletContext) {
        logger.info("get tomcat port from tomcat real path");
        String basePath = servletContext.getRealPath("/");
        logger.info("based from " + basePath + " to get tomcat server.xml");
        String serverXmlPath = (new File(basePath)).getParentFile().getParentFile() + File.separator + "conf" + File.separator + "server.xml";
        logger.info("found tomcat server.xml path " + serverXmlPath);

        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(new File(serverXmlPath));
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("/Server/Service[@name='Catalina']/Connector[count(@scheme)=0]/@port[1]");
            String result = (String) expr.evaluate(doc, XPathConstants.STRING);
            return result != null && result.length() > 0 ? Integer.valueOf(result) : null;
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }
}
