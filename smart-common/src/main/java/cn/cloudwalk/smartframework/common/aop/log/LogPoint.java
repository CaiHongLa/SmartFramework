package cn.cloudwalk.smartframework.common.aop.log;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 日志切入点，用于方法标注，使用框架提供的切面进行日志监控
 * <p>
 * date 2018/5/16 10:00
 *
 * @author liyanhui@cloudwalk.cn
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@Component
public @interface LogPoint {

    /**
     * 日志标记
     */
    String tag() default "";

    /**
     * 日志级别
     */
    LogLevel level() default LogLevel.INFO;

    /**
     * 日志级别
     */
    enum LogLevel {
        INFO,
        DEBUG,
        ERROR,
        WARN,
        TRACE
    }
}
