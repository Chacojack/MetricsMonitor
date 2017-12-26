# Metrics-Monitor

本代码用于学习和交流

## 开启监控

在配置上使用`MonitorEnable`注解，将自己要监控注解标志的类，加在后面，例如下面需要监控所有的Controller，Service，和XXController。那么可以这样配置：


```
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import com.xx.cashloan.annotations.XXControler;

@MonitorEnable({RestController.class, Controller.class, Service.class, XXControler.class})
```

## 设置Base Package

监控的时候可能有不同的根路径，可以通过`application.properties`文件中设置：

```
monitor.property.basePackages=com.xxx,com.yyy
```

或者通过注解设置basePackage字段：

```
@MonitorEnable(basePackages = {"com.sinafenqi"})
```


## 控制台打印监控数据

可以在`application.properties`文件中加上如下配置开启在控制台打印监控日志，用于调试

```
monitor.report.console = true
```

## Http获取监控数据的接口

> /monitor/metrics

## 接口获取的数据示例和含义

接口返回的是Json数据

```
{
    "version": "3.1.3",
    "gauges": {
        // 不关注
    },
    "counters": {
        // 不关注
    },
    "histograms": {
        // 不关注
    },
    "meters": {
        // 不关注
    },
    "timers": {
        // 方法对应的监控信息
        "xxx.xxx.xxx.method1": {
            "count": 1,   // 调用次数
            "max": 0.12226290000000001,  //最大执行时间
            "mean": 0.12226290000000001,
            "min": 0.12226290000000001,
            "p50": 0.12226290000000001,  
            "p75": 0.12226290000000001,  //75%情况执行时间在0.12226290000000001以下
            "p95": 0.12226290000000001,
            "p98": 0.12226290000000001,   
            "p99": 0.12226290000000001,
            "p999": 0.12226290000000001,  
            "stddev": 0,
            "m15_rate": 1.9447318850996619e-10,
            "m1_rate": 7.387537392187168e-104,  // 最近1分钟之内调用速率
            "m5_rate": 1.7872644992840657e-23,
            "mean_rate": 0.00002183716043462689,
            "duration_units": "seconds",      // 上面时间含义值得单位，
            "rate_units": "calls/second"   // 上面速率值得单位
        },
        "xxx.xxx.xxx.method2": {
            "count": 0,
            "max": 0,
            "mean": 0,
            "min": 0,
            "p50": 0,
            "p75": 0,
            "p95": 0,
            "p98": 0,
            "p99": 0,
            "p999": 0,
            "stddev": 0,
            "m15_rate": 0,
            "m1_rate": 0,
            "m5_rate": 0,
            "mean_rate": 0,
            "duration_units": "seconds",
            "rate_units": "calls/second"
        }
    }
}
```
