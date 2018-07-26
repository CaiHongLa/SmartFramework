package cn.cloudwalk.smartframework.common.domain.support;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.util.JPAUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 实体属性
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class DomainMetadata {
    private BaseDomain baseDomain;

    public DomainMetadata(BaseDomain baseDomain) {
        this.baseDomain = baseDomain;
    }

    public String getTableName() {
        return JPAUtil.getTableName(this.baseDomain.getClass());
    }

    public String getPKName() {
        return JPAUtil.getPKName(this.baseDomain.getClass());
    }

    public String getOriginalPKName() {
        return JPAUtil.getOriginalPKName(this.baseDomain.getClass());
    }

    public Serializable getPKValue() {
        return JPAUtil.getPKValue(this.baseDomain);
    }

    public List<TableColumnDef> getColumns() {
        return JPAUtil.getColumns(this.baseDomain.getClass());
    }

    public List<TableColumnDef> getNotNullValueColumns() {
        return JPAUtil.getNotNullValueColumns(this.baseDomain);
    }

    public List<TableColumnDef> getNullValueColumns() {
        return JPAUtil.getNullValueColumns(this.baseDomain);
    }
}

