package cn.cloudwalk.smartframework.common.protocol;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.model.BaseDataModel;
import cn.cloudwalk.smartframework.common.protocol.validate.ValidateResult;
import cn.cloudwalk.smartframework.common.util.JsonUtil;
import org.springframework.validation.BindingResult;

import java.util.Map;

/**
 * @author LIYANHUI
 */
public abstract class ProtocolIn extends BaseDataModel {
    private String tracker = "{}";

    public ProtocolIn() {
    }

    public void validate(ValidateResult validateResult, BindingResult annotationValidateResult) {
    }

    public ProtocolValidator getValidator() {
        return ProtocolValidator.getInstance().setCurrentProtocolIn(this);
    }

    public <T extends BaseDomain> T toDomain(Class<T> type) {
        return this.createAndCopyProperties(type);
    }

    public <T extends BaseDomain> T toValueObject(Class<T> type) {
        return this.createAndCopyProperties(type);
    }

    public void putTrackerItem(String name, String value) {
        Map<String, Object> map = this.getTrackerItems();
        map.put(name, value);
        this.tracker = JsonUtil.object2Json(map);
    }

    public Object getTrackerItem(String name) {
        return this.getTrackerItems().get(name);
    }

    public Map<String, Object> getTrackerItems() {
        return JsonUtil.json2Map(this.tracker);
    }

    public boolean containsTrackerItem(String name) {
        return this.getTrackerItems().containsKey(name);
    }

    public boolean containsTrackerItem(String name, String value) {
        return this.containsTrackerItem(name) && value.equals(this.getTrackerItem(name));
    }

    public String getTracker() {
        return this.tracker;
    }

    public void setTracker(String tracker) {
        this.tracker = tracker;
    }
}
