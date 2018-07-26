package cn.cloudwalk.smartframework.common.util.converter;

import com.google.gson.internal.LinkedTreeMap;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.*;

/**
 * @author LIYANHUI
 */
public class XStreamHashMapConverter extends AbstractCollectionConverter {
    public XStreamHashMapConverter(Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(LinkedTreeMap.class);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        this.process(source, writer);
    }

    @SuppressWarnings("unchecked")
    private void process(Object value, HierarchicalStreamWriter writer) {
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                writer.startNode(entry.getKey() + "");
                this.process(entry.getValue(), writer);
                writer.endNode();
            }
        } else if (value instanceof List) {
            List<Object> items = (List<Object>) value;
            for (Object item : items) {
                writer.startNode("value");
                this.process(item, writer);
                writer.endNode();
            }
        } else {
            String valueStr = value + "";
            if (valueStr.endsWith(".0")) {
                valueStr = valueStr.replaceFirst("\\.0$", "");
            }
            writer.setValue(valueStr);
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map<String, Object> map = new LinkedHashMap<>();
        this.process(map, reader);
        return map;
    }

    private void process(Map<String, Object> currMap, HierarchicalStreamReader reader) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (!reader.hasMoreChildren()) {
                String value = reader.getValue();
                if ("null".equals(value)) {
                    value = null;
                }

                currMap.put(reader.getNodeName() + "$" + System.nanoTime(), value);
                reader.moveUp();
            } else {
                Map<String, Object> subMap = new LinkedHashMap<>();
                this.process(subMap, reader);
                boolean isList = false;
                Set<String> keySet = subMap.keySet();
                Set<String> keyTmp = new HashSet<>();
                if (keySet.size() == 1 && "value".equals(keySet.iterator().next().replaceFirst("\\$\\d*", ""))) {
                    isList = true;
                } else {

                    for (String key : keySet) {
                        if (!keyTmp.add(key.split("\\$")[0])) {
                            isList = true;
                        }
                    }
                }

                if (isList) {
                    List<Object> list = new LinkedList<>();
                    list.addAll(subMap.values());
                    currMap.put(reader.getNodeName() + "$" + System.nanoTime(), list);
                } else {
                    currMap.put(reader.getNodeName() + "$" + System.nanoTime(), subMap);
                }

                reader.moveUp();
            }
        }

    }
}
