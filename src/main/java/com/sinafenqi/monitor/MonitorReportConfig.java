package com.sinafenqi.monitor;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import lombok.extern.java.Log;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;

@Configuration
@Log
@ConditionalOnProperty(prefix = "monitor.report", name = "console", havingValue = "true")
@Import(MonitorConfig.class)
public class MonitorReportConfig {


    @Bean
    public ConsoleReporter consoleReporter(MetricRegistry metrics) {
        log.info("Metrics Monitor enable console reporter.default is 6times/second");
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS);
        return reporter;
    }

}
