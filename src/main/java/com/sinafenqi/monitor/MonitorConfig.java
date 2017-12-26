package com.sinafenqi.monitor;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import com.sinafenqi.monitor.annotation.MonitorEnable;
import com.sinafenqi.monitor.proprety.MonitorProperty;
import lombok.extern.java.Log;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.Map;

@Configuration
@Log
public class MonitorConfig implements ImportAware, BeanFactoryPostProcessor {

    public MonitorProperty monitorProperty = new MonitorProperty();

    @Bean
    @ConfigurationProperties("monitor.property")
    public MonitorProperty monitorProperty() {
        return monitorProperty;
    }

    @Bean
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public MonitorAdvice monitorAdvice(MetricRegistry metricRegistry) {
        return new MonitorAdvice(metricRegistry);
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(MetricRegistry metricRegistry) {
        return new ServletRegistrationBean(new MetricsServlet(metricRegistry), "/monitor/metrics");
    }

    @Bean
    public MethodMonitorCenter methodMonitorCenter() {
        return new MethodMonitorCenter();
    }

    @Override
    public void setImportMetadata(AnnotationMetadata annotationMetadata) {
        log.info("setImportMetadata.");
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(MonitorEnable.class.getName(), false);
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(attributes);

        Class<? extends Annotation>[] aopClasses = (Class<? extends Annotation>[]) annotationAttributes.getClassArray("value");
        if (aopClasses == null || aopClasses.length == 0) {
            throw new RuntimeException("monitor cannot get aop annotation classes. nothing to monitor. Please use MonitorEnable annotation on your application.");
        }
        monitorProperty.setAopAnnotationClasses(aopClasses);
        monitorProperty.setBasePackages(annotationAttributes.getStringArray("basePackages"));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
        MonitorAdvice monitorAdvice = (MonitorAdvice) factory.getBean("monitorAdvice");
        Class<? extends Annotation>[] classes = monitorProperty.getAopAnnotationClasses();
        if (classes == null || classes.length == 0) {
            return;
        }
        for (Class<? extends Annotation> aClass : classes) {
            AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(aClass);
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(DefaultPointcutAdvisor.class.getName())
                    .addPropertyValue("pointcut", pointcut)
                    .addPropertyValue("advice", monitorAdvice)
                    .getBeanDefinition();
            factory.registerBeanDefinition("monitorAdvisor" + aClass.getName(), beanDefinition);
        }
    }
}
