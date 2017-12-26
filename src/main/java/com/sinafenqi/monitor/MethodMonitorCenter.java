package com.sinafenqi.monitor;

import com.codahale.metrics.MetricRegistry;
import com.sinafenqi.monitor.proprety.MonitorProperty;
import lombok.extern.java.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Log
public class MethodMonitorCenter implements ApplicationContextAware {

    @Autowired
    private MetricRegistry metricRegistry;
    @Autowired
    private MonitorProperty monitorProperty;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> monitorBeans = new HashMap<>();
        Class<? extends Annotation>[] classes = monitorProperty.getAopAnnotationClasses();
        if (classes == null || classes.length == 0) {
            return;
        }
        for (Class<? extends Annotation> aClass : classes) {
            monitorBeans.putAll(applicationContext.getBeansWithAnnotation(aClass));
        }

        log.info("monitor begin scan methods");
        monitorBeans.values().stream()
                .map(obj -> obj.getClass().getName())
                .map(this::trimString)
                .map(this::getClass)
                .filter(Objects::nonNull)
                .filter(this::isInPackages)
                .forEach(this::getClzMethods);
    }

    private boolean isInPackages(Class<?> clazz) {
        String[] basePackages = monitorProperty.getBasePackages();
        if (basePackages == null || basePackages.length == 0) {
            return true;
        }
        return Stream.of(basePackages).anyMatch(basePackage -> clazz.getName().startsWith(basePackage));
    }

    private Class<?> getClass(String clzName) {
        try {
            return Class.forName(clzName);
        } catch (Exception e) {
            return null;
        }
    }

    private void getClzMethods(Class<?> clz) {
        Stream.of(clz.getDeclaredMethods())
                .filter(method -> method.getName().indexOf('$') < 0)
                .forEach(method -> {
                    log.info("add method timer, method name ：" + method.toGenericString());
                    metricRegistry.timer(method.toString());
                });
    }

    private String trimString(String clzName) {
        if (clzName.indexOf('$') < 0) return clzName;
        return clzName.substring(0, clzName.indexOf('$'));
    }

}
