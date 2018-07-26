package cn.cloudwalk.smartframework.common.util.gson;

import cn.cloudwalk.smartframework.common.util.JsonUtil;
import com.google.gson.Gson;

/**
 * @author LIYANHUI
 */
public class GsonFactoryBeanForMessageConverter {
    public GsonFactoryBeanForMessageConverter() {
    }

    public Gson getGson() {
        return JsonUtil.getGson();
    }
}
