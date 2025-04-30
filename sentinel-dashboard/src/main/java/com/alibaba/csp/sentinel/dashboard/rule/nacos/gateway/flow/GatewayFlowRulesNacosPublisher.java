package com.alibaba.csp.sentinel.dashboard.rule.nacos.gateway.flow;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 修正类名拼写，Bean 名保持向后兼容
 */
@Component("gatewayFlowRulesNacosPublisher")
public class GatewayFlowRulesNacosPublisher
        extends AbstractNacosPublisher<GatewayFlowRuleEntity> {

    @Autowired
    public GatewayFlowRulesNacosPublisher(ConfigService configService,
                                          Converter<List<GatewayFlowRuleEntity>, String> encoder) {
        super(configService, encoder);
    }

    @Override protected DataIdPostfix postfix() { return DataIdPostfix.GW_FLOW; }
}
