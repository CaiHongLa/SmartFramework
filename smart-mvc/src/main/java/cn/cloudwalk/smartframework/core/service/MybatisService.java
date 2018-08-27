package cn.cloudwalk.smartframework.core.service;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.mvc.MvcComponent;
import cn.cloudwalk.smartframework.common.mvc.service.IMybatisService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;

/**
 * 使用Mybatis时的基础服务
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-27 上午11:03
 * @since 2.0.10
 */
//@Service("mybatisService")
public class MybatisService<Entity extends BaseDomain, Pk extends Serializable> extends MvcComponent implements IMybatisService<Entity, Pk> {

    @Autowired(
            required = false
    )
    protected Mapper<Entity> mapper;

    @Override
    public void save(Entity entity) {
        getMapper().insert(entity);
    }

    @Override
    public void delete(Entity entity) {
        getMapper().delete(entity);
    }

    @Override
    public void deleteById(Pk pk) {
        getMapper().deleteByPrimaryKey(pk);
    }

    @Override
    public void deleteByIds(List<Pk> pks) {
        for (Pk pk : pks) {
            getMapper().deleteByPrimaryKey(pk);
        }
    }

    @Override
    public void deleteAll(List<Entity> entities) {
        for (Entity entity : entities) {
            getMapper().delete(entity);
        }
    }

    @Override
    public void update(Entity entity) {
        getMapper().updateByPrimaryKey(entity);
    }

    @Override
    public Entity getById(Pk pk) {
        return getMapper().selectByPrimaryKey(pk);
    }

    @Override
    public boolean isExist(Pk pk) {
        Entity entity = getMapper().selectByPrimaryKey(pk);
        return entity != null;
    }

    @Override
    public List<Entity> selectByExample(Object example) {
        return getMapper().selectByExample(example);
    }

    @Override
    public void updateNotNull(Entity entity) {
        getMapper().updateByPrimaryKeySelective(entity);
    }

    @Override
    public int selectCountByExample(Object example) {
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
    public void updateByExampleSelective(Entity entity, Object example) {
        getMapper().updateByExampleSelective(entity, example);
    }

    @Override
    public Entity selectOne(Entity entity) {
        return getMapper().selectOne(entity);
    }

    @Override
    public void deleteByExample(Object example) {
        getMapper().deleteByExample(example);
    }

    @Override
    public Mapper<Entity> getMapper() {
        if (mapper == null) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("mybatis service must exist default mapper"));
        }
        return mapper;
    }

}
