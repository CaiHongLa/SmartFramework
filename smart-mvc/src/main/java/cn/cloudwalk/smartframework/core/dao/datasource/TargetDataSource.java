package cn.cloudwalk.smartframework.core.dao.datasource;

import java.lang.annotation.*;

/**
 * 使用此注解需要确定被该注解注解的方法必须是在同一个数据源
 *
 * @author LIYANHUI
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface TargetDataSource {
    String value();
}
