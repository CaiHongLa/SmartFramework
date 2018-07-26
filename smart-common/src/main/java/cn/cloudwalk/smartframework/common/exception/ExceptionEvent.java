package cn.cloudwalk.smartframework.common.exception;

import cn.cloudwalk.smartframework.common.event.BaseEvent;

/**
 * @author LIYANHUI
 */
public abstract class ExceptionEvent extends BaseEvent {
    public ExceptionEvent(Object source) {
        super(source);
    }
}
