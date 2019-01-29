package cn.cloudwalk.smartframework.core.dao.datasource;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @author LIYANHUI
 */
public class DataSourceUtil {

    public static void register(String beanName, Map<String, Object> properties, DATA_SOURCE_TYPE dataSourceType, ApplicationContext context) {
        if (context.containsBean(beanName)) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("data source " + beanName + " exist"));
        } else {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(dataSourceType.getClassName());
            if (properties != null && properties.size() > 0) {
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    String name = entry.getKey();
                    Object value = entry.getValue();
                    if (name != null) {
                        builder.addPropertyValue(name, value);
                    }
                }
            }
            factory.registerBeanDefinition(beanName, builder.getBeanDefinition());
        }
    }

    public enum DATA_SOURCE_TYPE {
        C3P0("com.mchange.v2.c3p0.ComboPooledDataSource");

        private String className;

        DATA_SOURCE_TYPE(String className) {
            this.className = className;
        }

        public static boolean containsByClassName(String className) {
            DATA_SOURCE_TYPE[] data_source_types = values();
            for (DATA_SOURCE_TYPE type : data_source_types) {
                if (type.getClassName().equals(className)) {
                    return true;
                }
            }
            return false;
        }

        public static boolean containsByName(String name) {
            DATA_SOURCE_TYPE[] data_source_types = values();
            for (DATA_SOURCE_TYPE type : data_source_types) {
                if (type.toString().equals(name)) {
                    return true;
                }
            }
            return false;
        }

        public String getClassName() {
            return this.className;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}