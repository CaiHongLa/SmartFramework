package cn.cloudwalk.smartframework.common.mvc.service;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.mvc.IMvcComponent;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

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

    int delete(Pk pk);

    int save(Entity entity);

    List<Entity> selectByExample(Example example);

    Entity selectByKey(Pk pk);

    int updateAll(Entity entity);

    int updateNotNull(Entity entity);

    int selectCountByExample(Example example);

    int selectCount(Entity entity);

    List<Entity> select(Entity entity);

    int insert(Entity entity);

    int updateByExampleSelective(Entity entity, Example example);

    Entity selectOne(Entity entity);

    int deleteByExample(Example example);

    int deleteByEntity(Entity entity);

    Mapper<Entity> getMapper();

}
