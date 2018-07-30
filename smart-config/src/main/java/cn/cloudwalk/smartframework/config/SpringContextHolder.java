package cn.cloudwalk.smartframework.config;

import cn.cloudwalk.smartframework.common.BaseComponent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Spring上下文
 *
 * date 2018/7/30 15:57
 *
 * @author liyanhui@cloudwalk.cn
 * @since 2.0.0
 */
@Component("springContextHolderFramework")
public class SpringContextHolder extends BaseComponent {

    /**
     * Spring上下文
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 获取Bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Object getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

}
