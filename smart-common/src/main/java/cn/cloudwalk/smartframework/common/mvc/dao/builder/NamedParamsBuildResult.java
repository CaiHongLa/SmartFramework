package cn.cloudwalk.smartframework.common.mvc.dao.builder;

import java.util.Arrays;

/**
 * @author LIYANHUI
 */
public class NamedParamsBuildResult {
    private String sql;
    private Object[] values;

    public NamedParamsBuildResult(String sql, Object[] values) {
        this.sql = sql;
        this.values = values;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getValues() {
        return this.values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "NamedParamsBuildResult [sql=" + this.sql + ", values=" + Arrays.toString(this.values) + "]";
    }
}
