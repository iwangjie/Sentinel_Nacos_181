package com.alibaba.csp.sentinel.dashboard.rule.nacos.flow;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("flowRuleNacosPublisher")
public class FlowRuleNacosPublisher
        extends AbstractNacosPublisher<FlowRuleEntity> {

    @Autowired
    public FlowRuleNacosPublisher(ConfigService configService,
                                  Converter<List<FlowRuleEntity>, String> encoder) {
        super(configService, encoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.FLOW;
    }
}
