package cn.cloudwalk.smartframework.core.service;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.mvc.MvcComponent;
import cn.cloudwalk.smartframework.common.mvc.dao.IBaseDao;
import cn.cloudwalk.smartframework.common.mvc.service.IBaseService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author LIYANHUI
 */
//@Service("baseService")
public class BaseService<Entity extends BaseDomain, Pk extends Serializable> extends MvcComponent implements IBaseService<Entity, Pk> {

    @Override
    public Serializable save(Entity entity) {
        return getDefaultDao().save(entity);
    }

    @Override
    public List<Serializable> saveAll(List<Entity> entities) {
        return getDefaultDao().saveAll(entities);
    }

    @Override
    public void updateAll(List<Entity> entities) {
        getDefaultDao().updateAll(entities);
    }

    @Override
    public void delete(Entity entity) {
        getDefaultDao().delete(entity);
    }

    @Override
    public void deleteById(Pk pk) {
        getDefaultDao().deleteById(pk);
    }

    @Override
    public void deleteByIds(List<Pk> pks) {
        getDefaultDao().deleteByIds(pks);
    }

    @Override
    public void deleteAll(List<Entity> entities) {
        getDefaultDao().deleteAll(entities);
    }

    @Override
    public void update(Entity entity) {
        getDefaultDao().update(entity);
    }

    @Override
    public Entity getById(Pk pk) {
        return getDefaultDao().getById(pk);
    }

    @Override
    public boolean isExist(Pk pk) {
        return getDefaultDao().isExist(pk);
    }

    @Override
    public List<Entity> getAll() {
        return getDefaultDao().getAll();
    }

    @Override
    public IBaseDao<Entity, Pk> getDefaultDao() {
        throw new FrameworkInternalSystemException(new SystemExceptionDesc("getDefaultDao() not override"));
    }
}
