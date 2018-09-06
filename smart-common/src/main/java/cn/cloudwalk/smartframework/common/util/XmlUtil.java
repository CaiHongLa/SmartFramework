package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.converter.XStreamHashMapConverter;
import com.google.gson.internal.LinkedTreeMap;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class XmlUtil {

    public static XStream xStream = new XStream();
    private static Logger logger = LogManager.getLogger(FileUtil.class);

    static {
        xStream.registerConverter(new XStreamHashMapConverter(new DefaultMapper(new ClassLoaderReference(XmlUtil.class.getClassLoader()))));
    }

    public XmlUtil() {
    }

    public static String xml2Json(String rootEleName, String xmlSrc) {
        xStream.alias(rootEleName, LinkedTreeMap.class);
        return JsonUtil.object2Json(xStream.fromXML(xmlSrc)).replaceAll("\\$\\d*", "");
    }

    public static Map<String, Object> xml2Map(String rootEleName, String xmlSrc) {
        return JsonUtil.json2Map(xml2Json(rootEleName, xmlSrc));
    }

    public static String xml2Json(String rootEleName, File xmlFile) {
        return xml2Json(rootEleName, xmlFile2String(xmlFile));
    }

    public static Map<String, Object> xml2Map(String rootEleName, File xmlFile) {
        return JsonUtil.json2Map(xml2Json(rootEleName, xmlFile2String(xmlFile)));
    }

    public static XStream getXStream() {
        return xStream;
    }

    private static String xmlFile2String(File xmlFile) {
        BufferedReader xmlFileReader = null;

        try {
            xmlFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile), StandardCharsets.UTF_8));
            StringBuilder xmlStringBuilder = new StringBuilder();
            String xmlLine;

            while ((xmlLine = xmlFileReader.readLine()) != null) {
                xmlStringBuilder.append(xmlLine);
            }

            return xmlStringBuilder.toString();
        } catch (IOException e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("read file error：" + e));
        } finally {
            try {
                if (xmlFileReader != null) {
                    xmlFileReader.close();
                }
            } catch (IOException e) {
                logger.error("close file error：" + e);
            }

        }
    }
}
