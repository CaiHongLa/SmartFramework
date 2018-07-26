package cn.cloudwalk.smartframework.common.mvc.dao;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.mvc.IMvcComponent;

import java.io.Serializable;

/**
 * @author LIYANHUI
 */
public interface IJdbcBaseDao<Entity extends BaseDomain, Pk extends Serializable> extends IBaseDao<Entity, Pk>, IMvcComponent {

}
