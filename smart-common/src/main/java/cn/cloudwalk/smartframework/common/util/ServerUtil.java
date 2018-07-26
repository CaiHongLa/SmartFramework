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
        logger.info("开始根据 tomcat 配置获取端口号");
        String basePath = servletContext.getRealPath("/");
        logger.info("准备以 " + basePath + " 为基路径获取 tomcat 的 server.xml 配置文件");
        String serverXmlPath = (new File(basePath)).getParentFile().getParentFile() + File.separator + "conf" + File.separator + "server.xml";
        logger.info("已定位到 tomcat server.xml 目录 " + serverXmlPath);
        Integer port;

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
