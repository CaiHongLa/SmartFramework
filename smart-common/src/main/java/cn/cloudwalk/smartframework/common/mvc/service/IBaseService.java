package cn.cloudwalk.smartframework.common.mvc.service;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.mvc.IMvcComponent;
import cn.cloudwalk.smartframework.common.mvc.dao.IBaseDao;

import java.io.Serializable;
import java.util.List;

/**
 * @author LIYANHUI
 */
public interface IBaseService<Entity extends BaseDomain, Pk extends Serializable> extends IMvcComponent {

    Serializable save(Entity entity);

    List<Serializable> saveAll(List<Entity> entities);

    void updateAll(List<Entity> entities);

    void delete(Entity entity);

    void deleteById(Pk pk);

    void deleteByIds(List<Pk> pks);

    void deleteAll(List<Entity> entities);

    void update(Entity entity);

    Entity getById(Pk pk);

    boolean isExist(Pk pk);

    List<Entity> getAll();

    IBaseDao<Entity, Pk> getDefaultDao();
}
