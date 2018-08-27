package cn.cloudwalk.smartframework.core.service;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.mvc.MvcComponent;
import cn.cloudwalk.smartframework.common.mvc.service.IMybatisService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * 使用Mybatis时的基础服务
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-27 上午11:03
 * @since 2.0.10
 */
public class MybatisService<Entity extends BaseDomain, Pk extends Serializable> extends MvcComponent implements IMybatisService<Entity, Pk> {

    @Autowired(
            required = false
    )
    protected Mapper<Entity> mapper;

    @Override
    public int delete(Pk pk) {
        return getMapper().deleteByPrimaryKey(pk);
    }

    @Override
    public int save(Entity entity) {
        return getMapper().insert(entity);
    }

    @Override
    public List<Entity> selectByExample(Example example) {
        return getMapper().selectByExample(example);
    }

    @Override
    public Entity selectByKey(Pk pk) {
        return getMapper().selectByPrimaryKey(pk);
    }

    @Override
    public int updateAll(Entity entity) {
        return getMapper().updateByPrimaryKey(entity);
    }

    @Override
    public int updateNotNull(Entity entity) {
        return getMapper().updateByPrimaryKeySelective(entity);
    }

    @Override
    public int selectCountByExample(Example example) {
        return getMapper().selectCountByExample(example);
    }

    @Override
    public int selectCount(Entity entity) {
        return getMapper().selectCount(entity);
    }

    @Override
    public List<Entity> select(Entity entity) {
        return getMapper().select(entity);
    }

    @Override
    public int insert(Entity entity) {
        return getMapper().insertSelective(entity);
    }

    @Override
    public int updateByExampleSelective(Entity entity, Example example) {
        return getMapper().updateByExampleSelective(entity, example);
    }

    @Override
    public Entity selectOne(Entity entity) {
        return getMapper().selectOne(entity);
    }

    @Override
    public int deleteByExample(Example example) {
        return getMapper().deleteByExample(example);
    }

    @Override
    public int deleteByEntity(Entity entity) {
        return getMapper().delete(entity);
    }

    @Override
    public Mapper<Entity> getMapper() {
        if (mapper == null) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("mybatis service must exist default mapper"));
        }
        return mapper;
    }

}
