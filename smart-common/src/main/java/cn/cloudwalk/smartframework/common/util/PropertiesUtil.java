package cn.cloudwalk.smartframework.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author LIYANHUI
 */
public class PropertiesUtil {

    public static Properties loadProperties(String filePath) {
        return FileUtil.loadProperties(filePath);
    }

    public static Properties loadClassPathProperties(String fileName) {
        return FileUtil.loadClassPathProperties(fileName);
    }

    public static Properties loadPropertiesOnClassPathOrConfigDir(String fileName) {
        return FileUtil.loadPropertiesOnClassPathOrConfigDir(fileName);
    }

    public static Map<String, Object> filter(String prefix, Properties properties) {
        return filter(prefix, null, false, properties);
    }

    public static Map<String, Object> filter(String prefix, boolean removePrefix, Properties properties) {
        return filter(prefix, null, removePrefix, properties);
    }

    public static Map<String, Object> filter(String prefix, String skipPattern, boolean removePrefix, Properties properties) {
        Map<String, Object> result = new HashMap<>();
        if (properties != null) {
            Iterator iterator = properties.stringPropertyNames().iterator();

            while (true) {
                String key;
                String simpleKey;
                do {
                    do {
                        if (!iterator.hasNext()) {
                            return result;
                        }

                        key = (String) iterator.next();
                    } while (!key.startsWith(prefix));

                    simpleKey = key;
                    if (removePrefix) {
                        simpleKey = key.replaceFirst(prefix, "");
                    }
                } while (skipPattern != null && key.replaceFirst(prefix, "").matches(skipPattern));

                result.put(simpleKey, properties.getProperty(key));
            }
        } else {
            return result;
        }
    }
}
