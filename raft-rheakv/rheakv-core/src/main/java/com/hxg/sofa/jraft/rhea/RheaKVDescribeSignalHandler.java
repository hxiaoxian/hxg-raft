package com.hxg.sofa.jraft.rhea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxg.sofa.jraft.util.Describer;
import com.hxg.sofa.jraft.util.FileOutputSignalHandler;
import com.hxg.sofa.jraft.util.SystemPropertyUtil;


public class RheaKVDescribeSignalHandler extends FileOutputSignalHandler {

    private static Logger       LOG       = LoggerFactory.getLogger(RheaKVDescribeSignalHandler.class);

    private static final String DIR       = SystemPropertyUtil.get("rheakv.signal.describe.dir", "");
    private static final String BASE_NAME = "rheakv_describe.log";

    @Override
    public void handle(final String signalName) {
        final List<Describer> describers = DescriberManager.getInstance().getAllDescribers();
        if (describers.isEmpty()) {
            return;
        }

        try {
            final File file = getOutputFile(DIR, BASE_NAME);

            LOG.info("Describing rheakv with signal: {} to file: {}.", signalName, file);

            try (final PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true),
                StandardCharsets.UTF_8))) {
                final Describer.Printer printer = new Describer.DefaultPrinter(out);
                for (final Describer describer : describers) {
                    describer.describe(printer);
                }
            }
        } catch (final IOException e) {
            LOG.error("Fail to describe rheakv: {}.", describers, e);
        }
    }
}
