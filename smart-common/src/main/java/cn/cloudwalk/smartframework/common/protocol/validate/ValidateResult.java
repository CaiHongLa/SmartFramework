package cn.cloudwalk.smartframework.common.protocol.validate;

import java.util.LinkedList;
import java.util.List;

/**
 * @author LIYANHUI
 */
public class ValidateResult {

    private List<ValidateResultItem> errors = new LinkedList<>();

    public void error(String code, String message) {
        this.errors.add(new ValidateResultItem(code, message));
    }

    public boolean hasErrors() {
        return this.errors.size() > 0;
    }

    public ValidateResultItem getFieldError() {
        return this.errors.size() > 0 ? this.errors.get(0) : null;
    }
}
