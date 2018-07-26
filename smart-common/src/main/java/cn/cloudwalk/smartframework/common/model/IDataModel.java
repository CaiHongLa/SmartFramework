package cn.cloudwalk.smartframework.common.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public interface IDataModel extends Serializable {

    void copyPropertiesTo(IDataModel dataModel);

    void copyPropertiesFromBean(IDataModel dataModel);

    void copyPropertiesFromMap(Map<String, Object> map);

    <T extends IDataModel> T createAndCopyProperties(Class<T> t);

    Map<String, Object> bean2Map();
}
