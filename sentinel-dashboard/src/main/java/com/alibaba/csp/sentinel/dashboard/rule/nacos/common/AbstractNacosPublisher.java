/* ===== common/abstractNacosPublisher.java ===== */
package com.alibaba.csp.sentinel.dashboard.rule.nacos.common;

import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 抽象父类：封装发布逻辑 & ACK 监听
 */
public abstract class AbstractNacosPublisher<R>
        implements DynamicRulePublisher<List<R>> {

    private final ConfigService configService;
    private final Converter<List<R>, String> encoder;

    protected AbstractNacosPublisher(ConfigService configService,
                                     Converter<List<R>, String> encoder) {
        this.configService = configService;
        this.encoder = encoder;
    }

    /**
     * 子类只需给出规则后缀
     */
    protected abstract DataIdPostfix postfix();

    @Override
    public void publish(String app, List<R> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }

        String dataId = postfix().dataId(app);
        boolean ok = configService.publishConfig(
                dataId, NacosConfigUtil.GROUP_ID, encoder.convert(rules));
//
//        if (!ok || !latch.await(10, TimeUnit.SECONDS)) {
//            throw new RuntimeException("Publish " + dataId + " timeout, please retry.");
//        }

//        CountDownLatch latch = new CountDownLatch(1);
//
//        Listener listener = new Listener() {
//            @Override
//            public Executor getExecutor() {
//                return null;
//            }
//
//            @Override
//            public void receiveConfigInfo(String config) {
//                latch.countDown();
//            }
//        };

//        try {
//            // 先注册监听，再发布
//            configService.addListener(dataId, NacosConfigUtil.GROUP_ID, listener);
//
//            boolean ok = configService.publishConfig(
//                    dataId, NacosConfigUtil.GROUP_ID, encoder.convert(rules));
//
//            if (!ok || !latch.await(10, TimeUnit.SECONDS)) {
//                throw new RuntimeException("Publish " + dataId + " timeout, please retry.");
//            }
//        } finally {
//            // 防止 Listener 泄漏
//            configService.removeListener(dataId, NacosConfigUtil.GROUP_ID, listener);
//        }
    }
}
