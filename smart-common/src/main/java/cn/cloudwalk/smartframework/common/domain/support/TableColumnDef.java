package cn.cloudwalk.smartframework.common.domain.support;

import javax.persistence.Column;

/**
 * 表字段定义
 *
 * @author LIYANHUI
 */
public class TableColumnDef {
    /**
     * 字段名
     */
    private String name;
    private Column definition;

    public TableColumnDef() {
    }

    public TableColumnDef(String name, Column definition) {
        this.name = name;
        this.definition = definition;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Column getDefinition() {
        return this.definition;
    }

    public void setDefinition(Column definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "TableColumnDef [name=" + this.name + ", definition=" + this.definition + "]";
    }
}