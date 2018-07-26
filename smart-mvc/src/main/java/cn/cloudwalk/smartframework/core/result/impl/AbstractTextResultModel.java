package cn.cloudwalk.smartframework.core.result.impl;

import cn.cloudwalk.smartframework.common.model.IDataModel;
import cn.cloudwalk.smartframework.core.result.AbstractResultModel;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public abstract class AbstractTextResultModel extends AbstractResultModel {

    @Override
    public void render(HttpServletResponse response) throws Exception {
        String text = this.getText();
        if (text != null) {
            response.getWriter().write(text);
        }

    }

    protected abstract String getText();

    @Override
    protected ContentType getResultContentType() {
        return ContentType.TEXT;
    }

    @Override
    public void copyPropertiesTo(IDataModel targetModel) {

    }

    @Override
    public void copyPropertiesFromBean(IDataModel sourceModel) {

    }

    @Override
    public void copyPropertiesFromMap(Map<String, Object> sourceMap) {

    }

    @Override
    public <T extends IDataModel> T createAndCopyProperties(Class<T> sourceModel) {
        return null;
    }


    @Override
    public Map<String, Object> bean2Map() {
        return null;
    }
}
