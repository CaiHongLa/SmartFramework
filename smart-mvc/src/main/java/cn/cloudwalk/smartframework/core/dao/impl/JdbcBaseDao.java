package cn.cloudwalk.smartframework.core.dao.impl;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.domain.support.DomainMetadata;
import cn.cloudwalk.smartframework.common.domain.support.SqlBuilder;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.mvc.MvcComponent;
import cn.cloudwalk.smartframework.common.mvc.dao.IJdbcBaseDao;
import cn.cloudwalk.smartframework.common.mvc.dao.builder.NamedParamsBuildResult;
import cn.cloudwalk.smartframework.common.mvc.dao.builder.NamedParamsBuilder;
import cn.cloudwalk.smartframework.common.util.JPAUtil;
import cn.cloudwalk.smartframework.common.util.ReflectUtil;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author LIYANHUI
 */
@Repository("jdbcBaseDao")
public class JdbcBaseDao<Entity extends BaseDomain, Pk extends Serializable> extends MvcComponent implements IJdbcBaseDao<Entity, Pk> {

    private static final Logger logger = LogManager.getLogger(JdbcBaseDao.class);

    @Autowired(
            required = false
    )
    @Qualifier("namedParameterJdbcTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private Class<Entity> entityClass;

    @Override
    @SuppressWarnings("unchecked")
    public Class<Entity> getEntityClass() {
        if (this.entityClass == null) {
            this.entityClass = (Class<Entity>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }

        return this.entityClass;
    }

    @Override
    public Serializable save(Entity entity) {
        final BeanPropertySqlParameterSource paramsBean = new BeanPropertySqlParameterSource(entity);
        DomainMetadata metadata = entity.getMetadata();
        String sql = entity.getSqlBuilder().generateInsertSql((column, baseDomain, metadata1, handler) -> {
        });
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.getNamedParameterJdbcTemplate().update(sql, paramsBean, keyHolder, new String[]{metadata.getOriginalPKName()});
        return this.buildGeneratedKey(keyHolder);
    }

    @Override
    public List<Serializable> saveAll(List<Entity> entities) {
        List<Serializable> ids = new LinkedList<>();
        if (entities != null && entities.size() > 0) {
            for (Entity entity : entities) {
                ids.add(this.save(entity));
            }
        }
        return ids;
    }

    @Override
    public void delete(Entity entity) {
        String sql = entity.getSqlBuilder().generateDeleteByIdSql();
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        this.getNamedParameterJdbcTemplate().update(sql, new BeanPropertySqlParameterSource(entity));
    }

    @Override
    public void deleteById(Pk id) {
        Class<Entity> entity = this.getEntityClass();
        String pkName = JPAUtil.getPKName(entity);
        String sql = SqlBuilder.generateDeleteByIdSql(entity);
        Map<String, Object> params = new HashMap<>();
        params.put(pkName, id);
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        this.getNamedParameterJdbcTemplate().update(sql, params);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteByIds(List<Pk> ids) {
        Class<? extends BaseDomain> entityCls = this.getEntityClass();
        String sql = SqlBuilder.generateDeleteByIdSql(entityCls);
        String pkName = JPAUtil.getPKName(entityCls);
        Map<String, Object>[] paramList = new HashMap[ids.size()];

        for (int i = 0; i < ids.size(); ++i) {
            Map<String, Object> item = new HashMap<>();
            item.put(pkName, ids.get(i));
            paramList[i] = item;
        }
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        this.getNamedParameterJdbcTemplate().batchUpdate(sql, paramList);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteAll(List<Entity> entities) {
        List<Pk> ids = new ArrayList<>();

        for (Entity entity : entities) {
            ids.add((Pk) entity.getMetadata().getPKValue());
        }

        this.deleteByIds(ids);
    }

    @Override
    public void update(Entity entity) {
        this.updateBase(entity);
    }

    @Override
    public void updateAll(List<Entity> entities) {
        if (entities != null && entities.size() > 0) {

            for (Entity entity : entities) {
                this.updateBase(entity);
            }
        }

    }

    @Override
    public int updateBySql(String sql) {
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        return this.getNamedParameterJdbcTemplate().update(sql, new EmptySqlParameterSource());
    }


    @Override
    public void execute(String sql) {
        logger.info(TextUtil.formatSql4DDL(sql));
        this.getNamedParameterJdbcTemplate().getJdbcOperations().execute(sql);
    }

    @Override
    public boolean isExist(Pk id) {
        Class<Entity> entity = this.getEntityClass();
        String pkName = JPAUtil.getPKName(entity);
        String sql = SqlBuilder.generateIsExistSql(entity);
        HashMap<String, Object> params = new HashMap<>();
        params.put(pkName, id);
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        BigDecimal count = this.getNamedParameterJdbcTemplate().queryForObject(sql, params, BigDecimal.class);
        return count.intValue() != 0;
    }

    @Override
    public Entity getById(Pk id) {
        Class<Entity> entity = this.getEntityClass();
        String pkName = JPAUtil.getPKName(entity);
        String sql = SqlBuilder.generateFindByIdSql(entity);
        HashMap<String, Object> params = new HashMap<>();
        params.put(pkName, id);
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        Map<String, Object> result;

        try {
            result = this.getNamedParameterJdbcTemplate().queryForMap(sql, params);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }

        try {
            Entity instance = entity.newInstance();
            instance.getDataHandler().copyValuesFromMap(result, true, property -> property.setName(property.getName().toLowerCase()));
            return instance;
        } catch (Exception exception) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
        }
    }

    @Override
    public List<Entity> getAll() {
        Class<Entity> entity = this.getEntityClass();
        String sql = SqlBuilder.generateFindAllSql(entity);
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        List<Map<String, Object>> result = this.getNamedParameterJdbcTemplate().queryForList(sql, new EmptySqlParameterSource());
        List<Entity> resultList = new ArrayList<>();

        try {

            for (Map<String, Object> record : result) {
                Entity instance = entity.newInstance();
                instance.getDataHandler().copyValuesFromMap(record, true, property -> property.setName(property.getName().toLowerCase()));
                resultList.add(instance);
            }

            return resultList;
        } catch (Exception exception) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
        }
    }

    @Override
    public Entity findOneBySql(String sql) {
        NamedParamsBuilder builder = NamedParamsBuilder.getInstance().empty();
        try {
            return this.findOneByNamedParamsSql(sql, builder);
        } finally {
            builder.clear();
        }
    }

    @Override
    public <V> V findOneBySqlForObjectValue(String sql, Class<V> type) {
        NamedParamsBuilder builder = NamedParamsBuilder.getInstance().empty();
        try {
            return this.findOneByNamedParamsSqlForObjectValue(sql, builder, type);
        } finally {
            builder.clear();
        }
    }

    @Override
    public Entity findOneByNamedParamsSql(String sql, NamedParamsBuilder builder) {
        Map<String, Object> params = builder.getAll();
        Map<String, Object> result;

        try {
            result = this.getNamedParameterJdbcTemplate().queryForMap(sql, params);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        } finally {
            builder.clear();
        }

        Class<Entity> entity = this.getEntityClass();

        try {
            Entity instance = entity.newInstance();
            instance.getDataHandler().copyValuesFromMap(result, true, property -> {
            });
            return instance;
        } catch (Exception exception) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
        }
    }


    @Override
    public Map<String, Object> findOneBySqlForMapValue(String sql) {
        NamedParamsBuilder builder = NamedParamsBuilder.getInstance().empty();
        try {
            return this.findOneByNamedParamsSqlForEntryValue(sql, builder);
        } finally {
            builder.clear();
        }
    }

    @Override
    public Map<String, Object> findOneByNamedParamsSqlForEntryValue(String sql, NamedParamsBuilder builder) {
        Map<String, Object> params = builder.getAll();
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        Map<String, Object> result;

        try {
            result = this.getNamedParameterJdbcTemplate().queryForMap(sql, params);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        } finally {
            builder.clear();
        }

        return result;
    }

    @Override
    public <V> V findFieldBySql(String sql, String fieldName, Class<V> type) {
        NamedParamsBuilder builder = NamedParamsBuilder.getInstance().empty();
        try {
            return this.findFieldByNamedParamsSql(sql, builder, fieldName, type);
        } finally {
            builder.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V findFieldByNamedParamsSql(String sql, NamedParamsBuilder builder, String fieldName, Class<V> type) {
        try {
            Map<String, Object> result = this.findOneByNamedParamsSqlForEntryValue(sql, builder);
            if (result != null) {
                V value = (V) result.get(fieldName);
                if (value != null) {
                    return value;
                }
            }
        } finally {
            builder.clear();
        }

        return null;
    }

    @Override
    public <V> V findFieldFirstBySql(String sql, Class<V> type) {
        return this.findFieldFirstByNamedParamsSql(sql, NamedParamsBuilder.getInstance().empty(), type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V findFieldFirstByNamedParamsSql(String sql, NamedParamsBuilder builder, Class<V> type) {
        try {
            Map<String, Object> result = this.findOneByNamedParamsSqlForEntryValue(sql, builder);
            if (result != null && result.values().size() > 0) {
                Object value = result.values().iterator().next();
                if (value != null) {
                    return (V) value;
                }
            }
        } finally {
            builder.clear();
        }

        return null;
    }

    @Override
    public List<Entity> findBySql(String sql) {
        NamedParamsBuilder builder = NamedParamsBuilder.getInstance().empty();
        try {
            return this.findByNamedParamsSql(sql, builder);
        } finally {
            builder.clear();
        }
    }

    @Override
    public <V> List<V> findBySqlForObjectValue(String sql, Class<V> type) {
        NamedParamsBuilder builder = NamedParamsBuilder.getInstance().empty();
        try {
            return this.findByNamedParamsSqlForObjectValue(sql, builder, type);
        } finally {
            builder.clear();
        }
    }

    @Override
    public int updateByNamedParamsSql(String sql, NamedParamsBuilder builder) {
        try {
            Map<String, Object> params = builder.getAll();
            logger.info(TextUtil.formatSql4DMLOrDQL(sql));
            return this.getNamedParameterJdbcTemplate().update(sql, params);
        } finally {
            builder.clear();
        }
    }

    @Override
    public List<Entity> findByNamedParamsSql(String sql, NamedParamsBuilder builder) {
        Map<String, Object> params = builder.getAll();
        Class<Entity> entity = this.getEntityClass();
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        List<Map<String, Object>> result = this.getNamedParameterJdbcTemplate().queryForList(sql, params);
        List<Entity> resultList = new ArrayList<>();
        if (result == null) {
            builder.clear();
            return resultList;
        }
        if (result.size() > 50) {
            logger.error("SQL : " + sql + "\nSQL params : " + params + "\nResult Size : " + result.size());
        }

        try {
            for (Map<String, Object> record : result) {
                Entity instance = entity.newInstance();
                instance.getDataHandler().copyValuesFromMap(record, true, property -> property.setName(property.getName().toLowerCase()));
                resultList.add(instance);
            }

            return resultList;
        } catch (Exception exception) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
        } finally {
            builder.clear();
        }
    }

    @Override
    public List<Map<String, Object>> findBySqlForMapValue(String sql) {
        NamedParamsBuilder builder = NamedParamsBuilder.getInstance().empty();
        try {
            return this.findByNamedParamsSqlForMapValue(sql, builder);
        } finally {
            builder.clear();
        }
    }

    @Override
    public List<Map<String, Object>> findByNamedParamsSqlForMapValue(String sql, NamedParamsBuilder builder) {
        try {
            Map<String, Object> params = builder.getAll();
            logger.info(TextUtil.formatSql4DMLOrDQL(sql));
            List<Map<String, Object>> result = this.getNamedParameterJdbcTemplate().queryForList(sql, params);
            if (result != null && result.size() > 50) {
                logger.error("SQL : " + sql + "\nSQL params : " + params + "\nResult Size : " + result.size());
            }

            return result;
        } finally {
            builder.clear();
        }
    }

    @Override
    public <V> List<V> findByNamedParamsSqlForObjectValue(String sql, NamedParamsBuilder builder, Class<V> type) {
        try {
            List<Map<String, Object>> result = this.findByNamedParamsSqlForMapValue(sql, builder);
            List<V> list = new ArrayList<>();
            if (result != null && result.size() > 0) {
                try {
                    for (Map<String, Object> row : result) {
                        V instance = type.newInstance();
                        ReflectUtil.copyTableData2Bean(instance, row);
                        list.add(instance);
                    }
                } catch (Exception exception) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
                }
            }

            return list;
        } finally {
            builder.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> batchUpdateByNamedParamsSql(String sql, NamedParamsBuilder namedParamsBuilder) {
        try {
            List<Map<String, Object>> paramsList = namedParamsBuilder.getAllForGroup();
            logger.info(TextUtil.formatSql4DMLOrDQL(sql));
            List<Integer> result = new LinkedList<>();
            int[] effectRows = this.getNamedParameterJdbcTemplate().batchUpdate(sql, paramsList.toArray(new HashMap[0]));

            for (int count : effectRows) {
                result.add(count);
            }
            return result;
        } finally {
            namedParamsBuilder.clear();
        }
    }

    @Override
    public void executeWithNamedParams(String sql, NamedParamsBuilder namedParamsBuilder) {
        try {
            this.executeComplexWithNamedParams(sql, namedParamsBuilder);
        } finally {
            namedParamsBuilder.clear();
        }
    }

    @Override
    public <V> V findOneByNamedParamsSqlForObjectValue(String sql, NamedParamsBuilder namedParamsBuilder, Class<V> type) {
        Map<String, Object> result = this.findOneByNamedParamsSqlForEntryValue(sql, namedParamsBuilder);
        if (result == null) {
            namedParamsBuilder.clear();
            return null;
        } else {
            try {
                V instance = type.newInstance();
                ReflectUtil.copyTableData2Bean(instance, result);
                return instance;
            } catch (IllegalAccessException | InstantiationException exception) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
            } finally {
                namedParamsBuilder.clear();
            }
        }
    }

    @Override
    public List<Map<String, Object>> findBySqlForListMapValue(String sql) {
        NamedParamsBuilder builder = NamedParamsBuilder.getInstance().empty();
        try {
            return this.findByNamedParamsSqlForMapValue(sql, builder);
        } finally {
            builder.clear();
        }
    }

    //*******************************************private methods*****************************************************

    private <V> void executeComplexWithNamedParams(String sql, NamedParamsBuilder namedParamsBuilder) {
        Map<String, Object> params = namedParamsBuilder.getAll();
        NamedParamsBuildResult result = NamedParamsBuilder.buildSqlAndParams(sql, params);
        String execSql = result.getSql();
        final Object[] values = result.getValues();
        logger.info(TextUtil.formatSql4DDL(execSql));
        this.getNamedParameterJdbcTemplate().getJdbcOperations().execute(execSql, (CallableStatementCallback<V>) cs -> {
            for (int i = 0; i < values.length; ++i) {
                cs.setObject(i + 1, values[i]);
            }
            cs.execute();
            return null;
        });
    }

    private void updateBase(Entity entity) {
        final BeanPropertySqlParameterSource paramsBean = new BeanPropertySqlParameterSource(entity);
        String sql = entity.getSqlBuilder().generateUpdateSql((column, baseDomain, metadata, handler) -> {

        });
        logger.info(TextUtil.formatSql4DMLOrDQL(sql));
        this.getNamedParameterJdbcTemplate().update(sql, paramsBean);
    }

    private Serializable buildGeneratedKey(KeyHolder keyHolder) {
        Map<String, Object> keyMap = keyHolder.getKeys();
        Serializable key = null;
        if (keyMap != null && keyMap.size() == 1) {
            Iterator<Object> it = keyMap.values().iterator();
            if (it.hasNext()) {
                Object keyValue = it.next();
                if (keyValue instanceof Serializable) {
                    key = (Serializable) keyValue;
                }
            }
        }

        return key;
    }

    private NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        if (this.namedParameterJdbcTemplate == null) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("error：spring.profiles.active is not standard"));
        } else {
            return this.namedParameterJdbcTemplate;
        }
    }
}
