package com.hxg.sofa.jraft.util;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

  
public class ThreadPoolMetricRegistry {

    private static final MetricRegistry             metricRegistry   = new MetricRegistry();
    private static final ThreadLocal<Timer.Context> timerThreadLocal = new ThreadLocal<>();

    /**
     * Return the global registry of metric instances.
     */
    public static MetricRegistry metricRegistry() {
        return metricRegistry;
    }

    public static ThreadLocal<Timer.Context> timerThreadLocal() {
        return timerThreadLocal;
    }
}
