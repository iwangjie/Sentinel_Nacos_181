package com.alibaba.csp.sentinel.dashboard.rule.nacos.degrade;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("degradeRuleNacosPublisher")
public class DegradeRuleNacosPublisher extends AbstractNacosPublisher<DegradeRuleEntity> {

    protected DegradeRuleNacosPublisher(ConfigService configService, Converter<List<DegradeRuleEntity>, String> encoder) {
        super(configService, encoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.DEGRADE;
    }
}
