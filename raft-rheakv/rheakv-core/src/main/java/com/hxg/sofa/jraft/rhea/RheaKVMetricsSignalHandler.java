package com.hxg.sofa.jraft.rhea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.rhea.metrics.KVMetrics;
import com.hxg.sofa.jraft.util.FileOutputSignalHandler;
import com.hxg.sofa.jraft.util.MetricReporter;
import com.hxg.sofa.jraft.util.SystemPropertyUtil;

  
public class RheaKVMetricsSignalHandler extends FileOutputSignalHandler {

    private static Logger       LOG       = LoggerFactory.getLogger(RheaKVMetricsSignalHandler.class);

    private static final String DIR       = SystemPropertyUtil.get("rheakv.signal.metrics.dir", "");
    private static final String BASE_NAME = "rheakv_metrics.log";

    @Override
    public void handle(final String signalName) {
        try {
            final File file = getOutputFile(DIR, BASE_NAME);

            LOG.info("Printing rheakv metrics with signal: {} to file: {}.", signalName, file);

            try (final PrintStream out = new PrintStream(new FileOutputStream(file, true))) {
                final MetricReporter reporter = MetricReporter.forRegistry(KVMetrics.metricRegistry()) //
                    .outputTo(out) //
                    .prefixedWith("-- rheakv") //
                    .build();
                reporter.report();
            }
        } catch (final IOException e) {
            LOG.error("Fail to print rheakv metrics.", e);
        }
    }
}
