package cn.cloudwalk.smartframework.common.util.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @author LIYANHUI
 */
public class GsonExclusionStrategy implements ExclusionStrategy {
    public GsonExclusionStrategy() {
    }

    @Override
    public boolean shouldSkipClass(Class<?> cls) {
        return cls == Throwable.class;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes attributes) {
        return false;
    }
}
