package cn.cloudwalk.smartframework.common.mvc.dao.builder;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.ReflectUtil;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.*;

/**
 * @author LIYANHUI
 */
public class NamedParamsBuilder {
    private static NamedParamsBuilder builder;
    private ThreadLocal<LinkedList<String>> keys = new ThreadLocal<>();
    private ThreadLocal<LinkedList<Object>> values = new ThreadLocal<>();
    private ThreadLocal<LinkedList<Map<String, Object>>> group = new ThreadLocal<>();
    private ThreadLocal<Boolean> isGroupMode = new ThreadLocal<>();

    private NamedParamsBuilder() {
    }

    public static NamedParamsBuilder getInstance() {
        if (builder == null) {
            builder = new NamedParamsBuilder();
        }

        return builder;
    }

    public static boolean integrityValidate(String sql, String[] keys) {
        for (String key : keys) {
            if (!sql.contains(key)) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("SQL 中缺少参数定义：" + key));
            }
        }

        return true;
    }

    public static NamedParamsBuildResult buildSqlAndParams(String sql, Map<String, Object> params) {
        return buildSqlAndParams(sql, params.keySet().toArray(new String[0]), params.values().toArray());
    }

    public static NamedParamsBuildResult buildSqlAndParams(String sql, String[] keys, Object[] values) {
        integrityValidate(sql, keys);
        List<Object[]> tmpList = new LinkedList<>();

        int startIndex;
        int keyLength;
        int pos;
        for (int i = 0; i < keys.length; ++i) {
            String key = keys[i];
            startIndex = 0;

            int position;
            for (; (position = sql.indexOf(":" + key, startIndex)) != -1; startIndex = position + keyLength) {
                keyLength = key.length();
                pos = position + keyLength + 1;
                if (pos >= sql.length()) {
                    tmpList.add(new Object[]{position, new Object[]{key, values[i]}});
                    break;
                }

                int nextChar = sql.charAt(pos);
                if (nextChar == ',' || nextChar == ')' || Character.isSpaceChar(nextChar) || Character.isWhitespace(nextChar)) {
                    tmpList.add(new Object[]{position, new Object[]{key, values[i]}});
                }
            }
        }

        int length = keys.length;

        for (startIndex = 0; startIndex < length; ++startIndex) {
            String key = keys[startIndex];
            keyLength = 0;

            for (; (pos = sql.indexOf(":" + key, keyLength)) != -1; keyLength = pos + 1) {
                keyLength = key.length();
                int index = pos + keyLength + 1;
                if (index >= sql.length()) {
                    sql = sql.substring(0, pos) + "?" + sql.substring(index);
                    break;
                }

                int nextChar = sql.charAt(index);
                if (nextChar == ',' || nextChar == ')' || Character.isSpaceChar(nextChar) || Character.isWhitespace(nextChar)) {
                    sql = sql.substring(0, pos) + "?" + sql.substring(index);
                }
            }
        }

        tmpList.sort((o1, o2) -> {
            Integer key1 = (Integer) o1[0];
            Integer key2 = (Integer) o2[0];
            if (key1.intValue() == key2.intValue()) {
                return 0;
            } else {
                return key1 > key2 ? 1 : -1;
            }
        });
        List<Object> _values = new LinkedList<>();

        for (Object[] item : tmpList) {
            _values.add(((Object[]) item[1])[1]);
        }

        return new NamedParamsBuildResult(sql, _values.toArray());
    }

    public Boolean isGroupMode() {
        if (this.isGroupMode.get() == null) {
            this.isGroupMode.set(false);
        }

        return isGroupMode.get();
    }

    public NamedParamsBuilder put(Object bean) {
        if (bean != null) {
            Map<String, Object> values = ReflectUtil.getPropertyValues(bean);
            if (values != null && values.size() > 0) {

                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null && !(value instanceof Collection) && !(value instanceof Map)) {
                        this.put(key, value);
                    }
                }
            }
        }

        return this;
    }

    public NamedParamsBuilder put(String key, Object value) {
        boolean mode = this.isGroupMode();
        if (!mode) {
            if (this.keys.get() == null) {
                this.keys.set(new LinkedList<>());
            }

            if (this.values.get() == null) {
                this.values.set(new LinkedList<>());
            }

            if ((this.keys.get()).contains(key)) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("参数重复：" + key));
            }

            (this.keys.get()).add(key);
            (this.values.get()).add(value);
        } else {
            ((this.group.get()).getLast()).put(key, value);
        }

        return this;
    }

    public String[] getKeys() {
        boolean mode = this.isGroupMode();
        if (mode) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("GROUP 模式下不能执行此方法"));
        } else {
            LinkedList<String> keys = this.keys.get();
            String[] result;
            if (keys != null && keys.size() > 0) {
                result = keys.toArray(new String[0]);
            } else {
                result = new String[0];
            }

            this.keys.remove();
            return result;
        }
    }

    public Object[] getValues() {
        boolean mode = this.isGroupMode();
        if (mode) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("GROUP 模式下不能执行此方法"));
        } else {
            LinkedList<Object> values = this.values.get();
            Object[] result;
            if (values != null && values.size() > 0) {
                result = values.toArray();
            } else {
                result = new Object[0];
            }

            this.values.remove();
            return result;
        }
    }

    public Map<String, Object> getAll() {
        boolean mode = this.isGroupMode();
        if (mode) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("GROUP 模式下不能执行此方法"));
        } else {
            Map<String, Object> all = new LinkedHashMap<>();
            String[] keys = this.getKeys();
            Object[] values = this.getValues();

            for (int i = 0; i < keys.length; ++i) {
                all.put(keys[i], values[i]);
            }

            return all;
        }
    }

    public NamedParamsBuilder empty() {
        this.keys.set(new LinkedList<>());
        this.values.set(new LinkedList<>());
        return this;
    }

    public SqlParameterSource toSqlParameterSource() {
        boolean mode = this.isGroupMode();
        if (mode) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("GROUP 模式下不能执行此方法"));
        } else {
            return new MapSqlParameterSource(this.getAll());
        }
    }

    public NamedParamsBuilder newGroup() {
        if (this.group.get() == null) {
            this.group.set(new LinkedList<>());
        }

        this.isGroupMode.set(true);
        (this.group.get()).add(new HashMap<>());
        return this;
    }

    public List<Map<String, Object>> getAllForGroup() {
        LinkedList<Map<String, Object>> data = this.group.get();
        this.group.remove();
        this.isGroupMode.remove();
        return data == null ? new LinkedList<>() : data;
    }

    public void clear() {
        this.isGroupMode.remove();
        this.keys.remove();
        this.values.remove();
        this.group.remove();
    }

    public SqlParameterSource[] toSqlParameterSourceForGroup() {
        List<SqlParameterSource> result = new ArrayList<>();
        LinkedList<Map<String, Object>> data = this.group.get();

        for (Map<String, Object> record : data) {
            result.add(new MapSqlParameterSource(record));
        }

        this.group.remove();
        this.isGroupMode.remove();
        return result.toArray(new SqlParameterSource[0]);
    }

    @Override
    public String toString() {
        StringBuilder desc = new StringBuilder();
        if (this.isGroupMode()) {
            desc.append("NamedParamsBuilder@[mode=group, group=").append(this.group.get()).append("]");
        } else {
            desc.append("NamedParamsBuilder@[mode=normal, keys=").append(this.keys.get()).append(", values=").append(this.values.get()).append("]");
        }

        return desc.toString();
    }
}
