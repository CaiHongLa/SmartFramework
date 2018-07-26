package cn.cloudwalk.smartframework.common.domain.support;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.util.JPAUtil;
import cn.cloudwalk.smartframework.common.util.ReflectUtil;
import cn.cloudwalk.smartframework.common.util.TextUtil;

import java.util.List;

/**
 * Sql生成
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class SqlBuilder {

    /**
     * 实体
     */
    private BaseDomain baseDomain;

    /**
     * 实体属性
     */
    private DomainMetadata metadata;

    /**
     * 实体数据
     */
    private DomainDataHandler handler;

    public SqlBuilder(BaseDomain baseDomain) {
        this.baseDomain = baseDomain;
        this.metadata = baseDomain.getMetadata();
        this.handler = baseDomain.getDataHandler();
    }

    public static String generateInsertSql(Class<? extends BaseDomain> entityCls, BaseDomain instance) {
        return generateInsertSql(entityCls, instance, null);
    }

    public static String generateInsertSql(Class<? extends BaseDomain> entityCls, BaseDomain instance, SqlBuilderScanner scr) {
        String tableName = JPAUtil.getTableName(entityCls);
        List<TableColumnDef> columns = JPAUtil.getColumns(entityCls);
        StringBuilder sql = new StringBuilder();
        StringBuilder columnsStr = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (TableColumnDef column : columns) {
            if (!ReflectUtil.isValueNull(column.getName(), instance)) {
                columnsStr.append(column.getDefinition().name()).append(",");
                placeholders.append(":").append(column.getName()).append(",");
                if (scr != null) {
                    scr.onPropertyScan(column.getName(), instance, null, null);
                }
            }
        }

        columnsStr = columnsStr.deleteCharAt(columnsStr.length() - 1);
        placeholders = placeholders.deleteCharAt(placeholders.length() - 1);
        sql.append("insert into ").append(tableName).append("(").append(columnsStr).append(") values(").append(placeholders).append(")");
        return sql.toString();
    }

    public static String generateUpdateSql(Class<? extends BaseDomain> entityCls, BaseDomain instance) {
        return generateUpdateSql(entityCls, instance, null);
    }

    public static String generateUpdateSql(Class<? extends BaseDomain> entityCls, BaseDomain instance, SqlBuilderScanner scr) {
        String pkName = JPAUtil.getPKName(entityCls);
        String originalPkName = TextUtil.unFormatVariableWithLower(pkName);
        String tableName = JPAUtil.getTableName(entityCls);
        List<TableColumnDef> columns = JPAUtil.getColumns(entityCls);
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(tableName).append(" set ");

        for (TableColumnDef column : columns) {
            if (!column.getName().equals(pkName) && !ReflectUtil.isValueNull(column.getName(), instance)) {
                sql.append(column.getDefinition().name()).append("=:").append(column.getName()).append(",");
                if (scr != null) {
                    scr.onPropertyScan(column.getName(), instance, null, null);
                }
            }
        }

        sql = sql.deleteCharAt(sql.length() - 1);
        sql.append(" where ").append(originalPkName).append("=:").append(pkName);
        return sql.toString();
    }

    public static String generateDeleteByIdSql(Class<? extends BaseDomain> entityCls) {
        StringBuilder sql = new StringBuilder();
        String pkName = JPAUtil.getPKName(entityCls);
        String originalPkName = TextUtil.unFormatVariableWithLower(pkName);
        sql.append("delete from ").append(JPAUtil.getTableName(entityCls)).append(" where ").append(originalPkName).append("=:").append(pkName);
        return sql.toString();
    }

    public static String generateFindAllSql(Class<? extends BaseDomain> entityCls) {
        return "select * from " + JPAUtil.getTableName(entityCls);
    }

    public static String generateFindByIdSql(Class<? extends BaseDomain> entityCls) {
        String tableName = JPAUtil.getTableName(entityCls);
        String pkName = JPAUtil.getPKName(entityCls);
        String originalPkName = TextUtil.unFormatVariableWithLower(pkName);
        return "select * from " + tableName + " where " + originalPkName + "=:" + pkName;
    }

    public static String generateIsExistSql(Class<? extends BaseDomain> entityCls) {
        String tableName = JPAUtil.getTableName(entityCls);
        String pkName = JPAUtil.getPKName(entityCls);
        String originalPkName = TextUtil.unFormatVariableWithLower(pkName);
        return "select count(1) from " + tableName + " where " + originalPkName + "=:" + pkName;
    }

    public String generateInsertSql() {
        return this.generateInsertSql(null);
    }

    public String generateInsertSql(SqlBuilderScanner scr) {
        StringBuilder sql = new StringBuilder();
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (TableColumnDef column : this.metadata.getColumns()) {
            if (!this.handler.isNull(column.getName())) {
                columns.append(column.getDefinition().name()).append(",");
                placeholders.append(":").append(column.getName()).append(",");
                if (scr != null) {
                    scr.onPropertyScan(column.getName(), this.baseDomain, this.metadata, this.handler);
                }
            }
        }

        columns = columns.deleteCharAt(columns.length() - 1);
        placeholders = placeholders.deleteCharAt(placeholders.length() - 1);
        sql.append("insert into ").append(this.metadata.getTableName()).append("(").append(columns).append(") values(").append(placeholders).append(")");
        return sql.toString();
    }

    public String generateUpdateSql() {
        return this.generateUpdateSql(null);
    }

    public String generateUpdateSql(SqlBuilderScanner scr) {
        String pkName = this.metadata.getPKName();
        String originalPkName = TextUtil.unFormatVariableWithLower(pkName);
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(this.metadata.getTableName()).append(" set ");

        for (TableColumnDef column : this.metadata.getColumns()) {
            if (!column.equals(pkName) && !this.handler.isNull(column.getName())) {
                sql.append(column.getDefinition().name()).append("=:").append(column.getName()).append(",");
                if (scr != null) {
                    scr.onPropertyScan(column.getName(), this.baseDomain, this.metadata, this.handler);
                }
            }
        }

        sql = sql.deleteCharAt(sql.length() - 1);
        sql.append(" where ").append(originalPkName).append("=:").append(pkName);
        return sql.toString();
    }

    public String generateDeleteByIdSql() {
        String pkName = this.metadata.getPKName();
        String originalPkName = TextUtil.unFormatVariableWithLower(pkName);
        return "delete from " + this.metadata.getTableName() + " where " + originalPkName + "=:" + pkName;
    }

    public String generateFindAllSql() {
        return "select * from " + this.metadata.getTableName();
    }

    public String generateFindByIdSql() {
        String pkName = this.metadata.getPKName();
        String originalPkName = TextUtil.unFormatVariableWithLower(pkName);
        return "select * from " + this.metadata.getTableName() + " where " + originalPkName + "=:" + pkName;
    }

    public String generateIsExistSql() {
        String tableName = this.metadata.getTableName();
        String pkName = this.metadata.getPKName();
        String originalPkName = TextUtil.unFormatVariableWithLower(pkName);
        return "select count(1) from " + tableName + " where " + originalPkName + "=:" + pkName;
    }
}
