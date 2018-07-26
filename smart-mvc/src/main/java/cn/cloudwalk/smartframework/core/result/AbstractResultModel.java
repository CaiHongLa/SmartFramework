package cn.cloudwalk.smartframework.core.result;

import cn.cloudwalk.smartframework.common.result.IResultModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public abstract class AbstractResultModel extends AbstractView implements IResultModel {

    protected static final Logger logger = LogManager.getLogger(AbstractResultModel.class);

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(this.getResultContentType().toString());
        this.render(response);
    }

    protected abstract void render(HttpServletResponse var1) throws Exception;

    protected abstract AbstractResultModel.ContentType getResultContentType();

    protected enum ContentType {
        TEXT("text/plain"),
        JAVASCRIPT("text/javascript"),
        JSON("text/json"),
        XML("text/xml"),
        HTML("text/html"),
        ZIP("application/zip"),
        STREAM("application/octet-stream");

        private String value;

        ContentType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
