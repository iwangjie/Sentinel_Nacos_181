package com.alibaba.csp.sentinel.metric.prom.collector;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.metric.prom.config.PrometheusGlobalConfig;
import com.alibaba.csp.sentinel.node.metric.MetricNode;
import com.alibaba.csp.sentinel.node.metric.MetricSearcher;
import com.alibaba.csp.sentinel.node.metric.MetricWriter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.util.PidUtil;
import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 * Sentinel ➟ Prometheus exporter without HistogramMetricFamily.
 * Compatible with simpleclient ≤ 0.8.x
 */
public class SentinelCollector extends Collector {

    /* ---------- static config ---------- */
    private static final String APP = PrometheusGlobalConfig.getPromFetchApp();
    private static final List<Double> BUCKETS = Arrays.asList(
            .005, .01, .025, .05, .1, .25, .5, 1d, 2.5, 5d, 10d);

    /* ---------- runtime state ---------- */
    private final Object lock = new Object();
    private volatile MetricSearcher searcher;
    private volatile long lastFetchMillis = 0L;

    /* cumulative traffic counters  (resource,classification,result) -> LongAdder */
    private final Map<List<String>, LongAdder> trafficTotals = new ConcurrentHashMap<>();

    /* latency bucket counters (resource,classification,le) -> LongAdder */
    private final Map<List<String>, LongAdder> latencyBuckets = new ConcurrentHashMap<>();

    /* latency sum&count  (resource,classification) -> Stat */
    private final Map<List<String>, LatStat> latencyStats = new ConcurrentHashMap<>();

    /* ---------- Collector impl ---------- */
    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> list = new ArrayList<>();
        initSearcherIfNeeded();
        List<MetricNode> nodes = safeFetch();
        if(nodes == null){
            return list;
        }
        accumulate(nodes);

        List<MetricFamilySamples> mfs = new ArrayList<>();

        /* ---- 1. traffic counter ---- */
        CounterMetricFamily reqTotal = new CounterMetricFamily(
                "sentinel_requests_total",
                "Total requests seen by Sentinel",
                Arrays.asList("app", "resource", "classification", "result"));
        trafficTotals.forEach((k, v) -> reqTotal.addMetric(prepend(APP, k), v.sum()));
        mfs.add(reqTotal);

        /* ---- 2. latency histogram (bucket/count/sum) ---- */
        buildLatencyFamilies(mfs);

        /* ---- 3. concurrency gauge ---- */
        GaugeMetricFamily conc = new GaugeMetricFamily(
                "sentinel_concurrency",
                "Current in-flight threads",
                Arrays.asList("app", "resource", "classification"));
        for (MetricNode n : nodes) {
            conc.addMetric(Arrays.asList(APP, n.getResource(), String.valueOf(n.getClassification())),
                    n.getConcurrency());
        }
        mfs.add(conc);

        /* ---- 4. rule inventory ---- */
        mfs.add(ruleGauge("flow", FlowRuleManager.getRules().size()));
        mfs.add(ruleGauge("degrade", DegradeRuleManager.getRules().size()));
        mfs.add(ruleGauge("system", SystemRuleManager.getRules().size()));
        mfs.add(ruleGauge("authority", AuthorityRuleManager.getRules().size()));
        mfs.add(ruleGauge("param", ParamFlowRuleManager.getRules().size()));

        /* ---- 5. scrape health ---- */
        GaugeMetricFamily up = new GaugeMetricFamily("sentinel_collector_up",
                "1 = last scrape OK", Collections.emptyList());
        up.addMetric(Collections.emptyList(), 1);
        mfs.add(up);

        lastFetchMillis = System.currentTimeMillis();
        return mfs;
    }

    /* ---------- helpers ---------- */

    private void initSearcherIfNeeded() {
        if (searcher != null) {
            return;
        } ;
        synchronized (lock) {
            if (searcher == null) {
                String file = MetricWriter.formMetricFileName(
                        SentinelConfig.getAppName(), PidUtil.getPid());
                searcher = new MetricSearcher(MetricWriter.METRIC_BASE_DIR, file);
                lastFetchMillis = System.currentTimeMillis() - 1000;
                RecordLog.info("[SentinelPrometheusCollector] searcher ready for {}", file);
            }
        }
    }

    private List<MetricNode> safeFetch() {
        try {
            return searcher.findByTimeAndResource(lastFetchMillis, System.currentTimeMillis(), null);
        } catch (Exception e) {
            RecordLog.warn("[SentinelPrometheusCollector] fetch failed", e);
            return Collections.emptyList();
        }
    }

    private void accumulate(List<MetricNode> nodes) {
        for (MetricNode n : nodes) {
            /* -------- traffic counters -------- */
            incrTraffic(n, "pass", n.getPassQps());
            incrTraffic(n, "block", n.getBlockQps());
            incrTraffic(n, "success", n.getSuccessQps());
            incrTraffic(n, "exception", n.getExceptionQps());

            /* -------- latency buckets + sum/count -------- */
            double rtSec = n.getRt() / 1000.0;      // ms ➟ s
            String res = n.getResource();
            String cls = String.valueOf(n.getClassification());

            for (double le : BUCKETS) {
                if (rtSec <= le) {
                    latencyBuckets
                            .computeIfAbsent(Arrays.asList(res, cls, String.valueOf(le)),
                                    k -> new LongAdder())
                            .increment();
                }
            }
            latencyBuckets
                    .computeIfAbsent(Arrays.asList(res, cls, "+Inf"),
                            k -> new LongAdder())
                    .increment();

            LatStat st = latencyStats.computeIfAbsent(
                    Arrays.asList(res, cls), k -> new LatStat());
            st.count.increment();
            st.sum.add(rtSec);
        }
    }

    private void incrTraffic(MetricNode n, String result, long delta) {
        trafficTotals
                .computeIfAbsent(Arrays.asList(
                                n.getResource(), String.valueOf(n.getClassification()), result),
                        k -> new LongAdder())
                .add(delta);
    }

    private void buildLatencyFamilies(List<MetricFamilySamples> mfs) {
        CounterMetricFamily bucketFam = new CounterMetricFamily(
                "sentinel_request_duration_seconds_bucket",
                "Latency histogram buckets",
                Arrays.asList("app", "resource", "classification", "le"));
        latencyBuckets.forEach((k, v) ->
                bucketFam.addMetric(prepend(APP, k), v.sum()));
        mfs.add(bucketFam);

        CounterMetricFamily countFam = new CounterMetricFamily(
                "sentinel_request_duration_seconds_count",
                "Latency sample count",
                Arrays.asList("app", "resource", "classification"));
        CounterMetricFamily sumFam = new CounterMetricFamily(
                "sentinel_request_duration_seconds_sum",
                "Latency sample total (seconds)",
                Arrays.asList("app", "resource", "classification"));

        latencyStats.forEach((k, stat) -> {
            countFam.addMetric(prepend(APP, k), stat.count.sum());
            sumFam.addMetric(prepend(APP, k), stat.sum.sum());
        });
        mfs.add(countFam);
        mfs.add(sumFam);
    }

    private GaugeMetricFamily ruleGauge(String type, int size) {
        GaugeMetricFamily g = new GaugeMetricFamily(
                "sentinel_rules",
                "Sentinel rule inventory",
                Arrays.asList("app", "rule_type"));
        g.addMetric(Arrays.asList(APP, type), size);
        return g;
    }

    private static List<String> prepend(String first, List<String> rest) {
        List<String> out = new ArrayList<>(rest.size() + 1);
        out.add(first);
        out.addAll(rest);
        return out;
    }

    /* holder for sum+count */
    private static final class LatStat {
        final LongAdder count = new LongAdder();
        final DoubleAdder sum = new DoubleAdder();
    }
}
