package com.alibaba.csp.sentinel.dashboard.rule.nacos.gateway.flow;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.stereotype.Component;

import java.util.List;


@Component("gatewayFlowRulesNacosProvider")
public class GatewayFlowRulesNacosProvider extends AbstractNacosProvider<GatewayFlowRuleEntity> {


    protected GatewayFlowRulesNacosProvider(ConfigService configService, Converter<String, List<GatewayFlowRuleEntity>> decoder) {
        super(configService, decoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.GW_FLOW;
    }
}
