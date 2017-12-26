package com.sinafenqi.monitor.annotation;

import com.sinafenqi.monitor.MonitorConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MonitorConfig.class)
public @interface MonitorEnable {

    Class<? extends Annotation>[] value();

    String[] basePackages() default {};

}
