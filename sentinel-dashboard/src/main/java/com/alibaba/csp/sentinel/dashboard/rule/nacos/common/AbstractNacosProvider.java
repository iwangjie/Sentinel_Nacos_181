/* ===== common/abstractNacosPublisher.java ===== */
package com.alibaba.csp.sentinel.dashboard.rule.nacos.common;

import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;

import java.util.Collections;
import java.util.List;

public abstract class AbstractNacosProvider<R>
        implements DynamicRuleProvider<List<R>> {


    protected final ConfigService configService;
    protected final Converter<String, List<R>> decoder;

    protected AbstractNacosProvider(ConfigService configService, Converter<String, List<R>> decoder) {
        this.configService = configService;
        this.decoder = decoder;
    }

    protected abstract DataIdPostfix postfix();

    @Override
    public List<R> getRules(String app) throws Exception {
        if (StringUtil.isBlank(app)) {
            return Collections.emptyList();
        }

        String raw = configService.getConfig(
                postfix().dataId(app), NacosConfigUtil.GROUP_ID, 3000);
        return StringUtil.isEmpty(raw) ? Collections.emptyList() : decoder.convert(raw);
    }
}
