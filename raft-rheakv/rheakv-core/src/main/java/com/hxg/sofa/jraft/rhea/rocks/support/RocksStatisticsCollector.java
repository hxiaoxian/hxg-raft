package com.hxg.sofa.jraft.rhea.rocks.support;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.rocksdb.HistogramData;
import org.rocksdb.HistogramType;
import org.rocksdb.Statistics;
import org.rocksdb.StatisticsCollectorCallback;
import org.rocksdb.StatsCollectorInput;
import org.rocksdb.TickerType;

import com.hxg.sofa.jraft.rhea.util.concurrent.NamedThreadFactory;

/**
 * Helper class to collect rocksDB statistics periodically at a period specified
 * in constructor. Callback function (provided in constructor) is called with
 * every statistics collection.
 * <p>
 * Caller should call start() to start statistics collection. Shutdown() should
 * be called to stop stats collection and should be called before statistics (
 * provided in constructor) reference has been disposed.
 *
 */
public class RocksStatisticsCollector {

    private final CopyOnWriteArrayList<StatsCollectorInput> statsCollectorInputList = new CopyOnWriteArrayList<>();
    private final long                                      statsCollectionIntervalInMillis;
    private final ExecutorService                           executorService;
    private volatile boolean                                isRunning               = true;

    public RocksStatisticsCollector(final long statsCollectionIntervalInMillis) {
        this.statsCollectionIntervalInMillis = statsCollectionIntervalInMillis;
        this.executorService = Executors.newSingleThreadExecutor(new NamedThreadFactory("rocks-statistics-collector",
            true));
    }

    public void start() {
        this.executorService.submit(collectStatistics());
    }

    public void addStatsCollectorInput(final StatsCollectorInput input) {
        statsCollectorInputList.add(input);
    }

    /**
     * Shuts down statistics collector.
     *
     * @param shutdownTimeout Time in milli-seconds to wait for shutdown before
     *                        killing the collection process.
     * @throws InterruptedException thrown if Threads are interrupted.
     */
    public void shutdown(final int shutdownTimeout) throws InterruptedException {
        this.isRunning = false;

        this.executorService.shutdownNow();
        // Wait for collectStatistics runnable to finish so that disposal of
        // statistics does not cause any exceptions to be thrown.
        this.executorService.awaitTermination(shutdownTimeout, TimeUnit.MILLISECONDS);
    }

    private Runnable collectStatistics() {
        return () -> {
            while (this.isRunning) {
                try {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    for (final StatsCollectorInput statsCollectorInput : this.statsCollectorInputList) {
                        final Statistics statistics = statsCollectorInput.getStatistics();
                        final StatisticsCollectorCallback statsCallback = statsCollectorInput.getCallback();
                        // Collect ticker data
                        for (final TickerType ticker : TickerType.values()) {
                            if (ticker != TickerType.TICKER_ENUM_MAX) {
                                long tickerValue = statistics.getTickerCount(ticker);
                                statsCallback.tickerCallback(ticker, tickerValue);
                            }
                        }
                        // Collect histogram data
                        for (final HistogramType histogramType : HistogramType.values()) {
                            if (histogramType != HistogramType.HISTOGRAM_ENUM_MAX) {
                                HistogramData histogramData = statistics.getHistogramData(histogramType);
                                statsCallback.histogramCallback(histogramType, histogramData);
                            }
                        }
                    }
                    Thread.sleep(this.statsCollectionIntervalInMillis);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (final Exception e) {
                    throw new RuntimeException("Error while calculating statistics", e);
                }
            }
        };
    }
}
