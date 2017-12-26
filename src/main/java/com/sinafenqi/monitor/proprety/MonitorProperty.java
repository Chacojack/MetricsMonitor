package com.sinafenqi.monitor.proprety;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitorProperty {

    private Class<? extends Annotation>[] aopAnnotationClasses;

    private String[] basePackages;

}
