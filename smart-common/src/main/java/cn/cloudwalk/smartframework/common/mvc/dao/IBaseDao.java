package cn.cloudwalk.smartframework.common.mvc.dao;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.mvc.IMvcComponent;
import cn.cloudwalk.smartframework.common.mvc.dao.builder.NamedParamsBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public interface IBaseDao<Entity extends BaseDomain, Pk extends Serializable> extends IMvcComponent {

    Class<Entity> getEntityClass();

    Serializable save(Entity entity);

    List<Serializable> saveAll(List<Entity> entities);

    void delete(Entity entity);

    void deleteById(Pk pk);

    void deleteByIds(List<Pk> pks);

    void deleteAll(List<Entity> entities);

    void update(Entity entity);

    void updateAll(List<Entity> entities);

    int updateBySql(String sql);

    boolean isExist(Pk pk);

    void execute(String sql);

    Entity getById(Pk pk);

    List<Entity> getAll();

    Entity findOneBySql(String sql);

    Map<String, Object> findOneBySqlForMapValue(String sql);

    <V> V findOneBySqlForObjectValue(String sql, Class<V> type);

    <V> V findFieldBySql(String sql, String fieldName, Class<V> type);

    <V> V findFieldFirstBySql(String sql, Class<V> type);

    List<Entity> findBySql(String sql);

    List<Map<String, Object>> findBySqlForListMapValue(String sql);

    <V> List<V> findBySqlForObjectValue(String sql, Class<V> type);

    int updateByNamedParamsSql(String sql, NamedParamsBuilder builder);

    List<Integer> batchUpdateByNamedParamsSql(String sql, NamedParamsBuilder builder);

    void executeWithNamedParams(String sql, NamedParamsBuilder builder);

    Entity findOneByNamedParamsSql(String sql, NamedParamsBuilder builder);

    Map<String, Object> findOneByNamedParamsSqlForEntryValue(String sql, NamedParamsBuilder builder);

    <V> V findOneByNamedParamsSqlForObjectValue(String sql, NamedParamsBuilder builder, Class<V> type);

    <V> V findFieldByNamedParamsSql(String sql, NamedParamsBuilder builder, String fieldName, Class<V> type);

    <V> V findFieldFirstByNamedParamsSql(String sql, NamedParamsBuilder builder, Class<V> type);

    List<Map<String, Object>> findBySqlForMapValue(String sql);

    List<Entity> findByNamedParamsSql(String sql, NamedParamsBuilder builder);

    List<Map<String, Object>> findByNamedParamsSqlForMapValue(String sql, NamedParamsBuilder builder);

    <V> List<V> findByNamedParamsSqlForObjectValue(String sql, NamedParamsBuilder builder, Class<V> type);

}
