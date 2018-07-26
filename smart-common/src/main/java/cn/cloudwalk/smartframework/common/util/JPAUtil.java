package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.domain.support.DomainDefinitionException;
import cn.cloudwalk.smartframework.common.domain.support.TableColumnDef;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LIYANHUI
 */
public class JPAUtil {

    public static String getTableName(Class<? extends BaseDomain> cls) {
        checkIsExtendFromBaseDomain(cls);
        Table table = cls.getAnnotation(Table.class);
        if (table != null) {
            return table.name();
        } else {
            throw new DomainDefinitionException(new SystemExceptionDesc("domain（" + cls.getName() + "） 定义异常，缺少 @Table 定义"));
        }
    }

    public static String getPKName(Class<? extends BaseDomain> cls) {
        checkIsExtendFromBaseDomain(cls);

        try {
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("get")) {
                    String propertyName = TextUtil.getGetterPropertyName(methodName);
                    if (method.isAnnotationPresent(Id.class) || cls.getDeclaredField(propertyName).isAnnotationPresent(Id.class)) {
                        return propertyName;
                    }
                }
            }
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }

        throw new DomainDefinitionException(new SystemExceptionDesc("domain（" + cls.getName() + "） 定义异常，缺少 @Id 定义"));
    }

    public static String getOriginalPKName(Class<? extends BaseDomain> cls) {
        return TextUtil.unFormatVariableWithLower(getPKName(cls));
    }

    public static Serializable getPKValue(BaseDomain instance) {
        Class<? extends BaseDomain> cls = instance.getClass();
        checkIsExtendFromBaseDomain(cls);
        String pkName = getPKName(cls);
        String getterMethodName = "get" + pkName.substring(0, 1).toUpperCase() + pkName.substring(1);

        try {
            Object result = cls.getMethod(getterMethodName, (Class[]) null).invoke(instance, (Object[]) null);
            if (result == null) {
                return null;
            } else if (result instanceof Serializable) {
                return (Serializable) result;
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("主键必须属于 Serializable"));
            }
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public static List<TableColumnDef> getColumns(Class<? extends BaseDomain> cls) {
        try {
            return getColumns(cls.newInstance(), JPAUtil.COLUMN_FILTER.ALL);
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public static List<TableColumnDef> getColumns(BaseDomain instance) {
        return getColumns(instance, JPAUtil.COLUMN_FILTER.ALL);
    }

    public static List<TableColumnDef> getNotNullValueColumns(BaseDomain instance) {
        return getColumns(instance, JPAUtil.COLUMN_FILTER.NOT_NULl_VALUE);
    }

    public static List<TableColumnDef> getNullValueColumns(BaseDomain instance) {
        return getColumns(instance, JPAUtil.COLUMN_FILTER.NULL_VALUE);
    }

    public static List<TableColumnDef> getColumns(BaseDomain instance, JPAUtil.COLUMN_FILTER filter) {
        List<TableColumnDef> columns = new ArrayList<>();

        try {
            Class<? extends BaseDomain> cls = instance.getClass();
            checkIsExtendFromBaseDomain(cls);
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("get")) {
                    String propertyName = TextUtil.getGetterPropertyName(methodName);
                    Column colDef = null;
                    if (!method.isAnnotationPresent(Column.class) && !method.isAnnotationPresent(JoinColumn.class)) {
                        if (cls.getDeclaredField(propertyName).isAnnotationPresent(Column.class) || cls.getDeclaredField(propertyName).isAnnotationPresent(JoinColumn.class)) {
                            colDef = cls.getDeclaredField(propertyName).getAnnotation(Column.class);
                        }
                    } else {
                        colDef = method.getAnnotation(Column.class);
                    }

                    if (colDef != null) {
                        if (filter == COLUMN_FILTER.ALL) {
                            columns.add(new TableColumnDef(propertyName, colDef));
                        } else {
                            boolean isNull = ReflectUtil.isValueNull(propertyName, instance);
                            if (filter == COLUMN_FILTER.NOT_NULl_VALUE && !isNull) {
                                columns.add(new TableColumnDef(propertyName, colDef));
                            } else if (filter == COLUMN_FILTER.NULL_VALUE && isNull) {
                                columns.add(new TableColumnDef(propertyName, colDef));
                            }
                        }
                    }
                }
            }

            return columns;
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    private static void checkIsExtendFromBaseDomain(Class<? extends BaseDomain> cls) {
        if (cls.getSuperclass() != BaseDomain.class) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("实体必须直接继承自 BaseDomain"));
        }
    }

    public enum COLUMN_FILTER {
        ALL,
        NOT_NULl_VALUE,
        NULL_VALUE;

        COLUMN_FILTER() {
        }
    }

    public enum VALUE_TYPE {
        STRING,
        INTEGER,
        LONG;

        VALUE_TYPE() {
        }
    }
}
