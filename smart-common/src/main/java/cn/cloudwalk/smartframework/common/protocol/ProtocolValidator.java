package cn.cloudwalk.smartframework.common.protocol;

import cn.cloudwalk.smartframework.common.exception.desc.impl.ProtocolExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.exception.exception.InvalidProtocolParamsException;
import cn.cloudwalk.smartframework.common.protocol.validate.ValidateResult;
import cn.cloudwalk.smartframework.common.protocol.validate.ValidateResultItem;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * @author LIYANHUI
 */
public class ProtocolValidator {

    private static ProtocolValidator validator;
    private ThreadLocal<ProtocolIn> currentProtocolIn = new ThreadLocal<>();

    private ProtocolValidator() {
    }

    public static ProtocolValidator getInstance() {
        if (validator == null) {
            validator = new ProtocolValidator();
        }

        return validator;
    }

    public ProtocolValidator setCurrentProtocolIn(ProtocolIn in) {
        this.currentProtocolIn.set(in);
        return this;
    }

    public void interruptIfHasError(BindingResult result) {
        try {
            if (result.hasErrors()) {
                FieldError field = result.getFieldError();
                String code = field.getCode();
                String message = field.getDefaultMessage();
                if ("typeMismatch".equals(code)) {
                    throw new InvalidProtocolParamsException(new ProtocolExceptionDesc("400000", "typeMismatch，please check " + field.getField() + " type"));
                } else if (message != null && message.contains(":")) {
                    String[] part = message.split(":");
                    throw new InvalidProtocolParamsException(new ProtocolExceptionDesc(part[0], part[1]));
                } else {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc("field valid failed，for not config message or message not right"));
                }
            } else {
                ProtocolIn in = this.currentProtocolIn.get();
                if (in != null) {
                    this.currentProtocolIn.remove();
                    ValidateResult validateResult = new ValidateResult();
                    in.validate(validateResult, result);
                    if (validateResult.hasErrors()) {
                        ValidateResultItem firstError = validateResult.getFieldError();
                        throw new InvalidProtocolParamsException(new ProtocolExceptionDesc(firstError.getCode(), firstError.getMessage()));
                    }
                }

            }
        }finally {
            currentProtocolIn.remove();
        }
    }
}
