package com.alibaba.csp.sentinel.dashboard.rule.nacos.system;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("systemRuleNacosPublisher")
public class SystemRuleNacosPublisher
        extends AbstractNacosPublisher<SystemRuleEntity> {

    @Autowired
    public SystemRuleNacosPublisher(ConfigService configService,
                                    Converter<List<SystemRuleEntity>, String> encoder) {
        super(configService, encoder);
    }

    @Override protected DataIdPostfix postfix() { return DataIdPostfix.SYSTEM; }
}
