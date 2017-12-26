package com.sinafenqi.monitor;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import lombok.extern.java.Log;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Log
public class MonitorAdvice implements MethodInterceptor {

    MetricRegistry metricRegistry;

    public MonitorAdvice(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().toString();
        log.info("monitor invoke. method: " + methodName);
        boolean contains = metricRegistry.getNames().contains(methodName);
        if (!contains) {
            return invocation.proceed();
        }
        log.info("monitor start method = [" + methodName + "]");
        Timer timer = metricRegistry.timer(methodName);
        Timer.Context context = timer.time();
        try {
            return invocation.proceed();
        } finally {
            context.stop();
        }
    }

}
