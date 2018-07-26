package cn.cloudwalk.smartframework.config.log;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.aop.log.LogPoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 日志切入点切面实现，用于切入标注了LogPoint的方法
 * <p>
 * date 2018/5/16 10:02
 *
 * @author liyanhui@cloudwalk.cn
 * @since 2.0.0
 */
@Aspect
@Component
public class LogPointService extends BaseComponent {

    @Pointcut("execution(* cn.cloudwalk..*(..)) && (@target(org.springframework.stereotype.Component) || @target(org.springframework.stereotype.Service) || @target(org.springframework.stereotype.Controller) || @target(org.springframework.stereotype.Repository))")
    private void defineAnnotationJoinPointExpression() {
    }

    @Before(value = "defineAnnotationJoinPointExpression() && @annotation(logPoint)")
    public void before(JoinPoint joinPoint, LogPoint logPoint) {
        ClazzData clazzData = getClazzData(joinPoint);
        Object[] args = joinPoint.getArgs();
        StringBuilder paramBuilder = new StringBuilder(" params : { ");
        for (int i = 0; i < args.length; i++) {
            paramBuilder.append("param[").append(i).append("]: ").append(args[i] == null ? "null" : args[i]).append(",");
        }
        paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        paramBuilder.append("}");
        String logMessage = clazzData.getClassName() + "." + clazzData.getMethodName() + " Start ! tag: " + logPoint.tag() + paramBuilder.toString();
        log(logPoint.level(), logMessage);
    }

    @Around(value = "defineAnnotationJoinPointExpression() && @annotation(logPoint)")
    public Object around(ProceedingJoinPoint joinPoint, LogPoint logPoint) throws Throwable {
        ClazzData clazzData = getClazzData(joinPoint);
        Object result = joinPoint.proceed();
        String logMessage = clazzData.getClassName() + "." + clazzData.getMethodName() + " End ! tag: " + logPoint.tag() + " result: " + result;
        log(logPoint.level(), logMessage);
        return result;
    }

    private void log(LogPoint.LogLevel logLevel, String message) {
        switch (logLevel) {
            case DEBUG:
                logger.debug(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case TRACE:
                logger.trace(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            default:
                logger.info(message);
                break;
        }
    }

    private ClazzData getClazzData(JoinPoint joinPoint) {
        Class targetClass = joinPoint.getTarget().getClass();
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = ms.getMethod();
        String className = targetClass.getName();
        String methodName = targetMethod.getName();
        ClazzData clazzData = new ClazzData();
        clazzData.setClassName(className);
        clazzData.setMethodName(methodName);
        clazzData.setMs(ms);
        clazzData.setTargetClass(targetClass);
        clazzData.setTargetMethod(targetMethod);
        return clazzData;
    }

    static final class ClazzData {
        Class targetClass;
        MethodSignature ms;
        Method targetMethod;
        String className;
        String methodName;

        Class getTargetClass() {
            return targetClass;
        }

        void setTargetClass(Class targetClass) {
            this.targetClass = targetClass;
        }

        MethodSignature getMs() {
            return ms;
        }

        void setMs(MethodSignature ms) {
            this.ms = ms;
        }

        Method getTargetMethod() {
            return targetMethod;
        }

        void setTargetMethod(Method targetMethod) {
            this.targetMethod = targetMethod;
        }

        String getClassName() {
            return className;
        }

        void setClassName(String className) {
            this.className = className;
        }

        String getMethodName() {
            return methodName;
        }

        void setMethodName(String methodName) {
            this.methodName = methodName;
        }
    }
}
