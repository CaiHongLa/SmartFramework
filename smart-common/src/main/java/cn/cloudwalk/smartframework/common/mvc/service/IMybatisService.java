package cn.cloudwalk.smartframework.common.mvc.service;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.mvc.IMvcComponent;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;

/**
 * IMybatisService
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-27 上午11:05
 * @since 2.0.10
 */
public interface IMybatisService<Entity extends BaseDomain, Pk extends Serializable> extends IMvcComponent {

    void save(Entity entity);

    void delete(Entity entity);

    void deleteById(Pk pk);

    void deleteByIds(List<Pk> pks);

    void deleteAll(List<Entity> entities);

    void update(Entity entity);

    Entity getById(Pk pk);

    boolean isExist(Pk pk);

    List<Entity> selectByExample(Object example);

    void updateNotNull(Entity entity);

    int selectCountByExample(Object example);

    int selectCount(Entity entity);

    List<Entity> select(Entity entity);

    void updateByExampleSelective(Entity entity, Object example);

    Entity selectOne(Entity entity);

    void deleteByExample(Object example);

    Mapper<Entity> getMapper();

}
