package cn.cloudwalk.smartframework.common.domain.support;

import java.util.Map;

/**
 * 属性设置
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface PropertySetter {

    void set(PropertySetter.Property property);

    class Property {
        private String name;
        private Object value;

        public Property() {
        }

        public Property(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public Property(Map.Entry<String, Object> entry) {
            this.name = entry.getKey();
            this.value = entry.getValue();
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
