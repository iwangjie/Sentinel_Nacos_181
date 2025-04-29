package com.alibaba.csp.sentinel.dashboard.rule.nacos.gateway.flow;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 拉取Nacos中存储的网关流控规则配置信息
 *
 * @author 星空流年
 */
@Component("gatewayFlowRulesNacosProvider")
public class GatewayFlowRulesNacosProvider implements DynamicRuleProvider<List<GatewayFlowRuleEntity>> {

    @Autowired
    private ConfigService configService;

    @Autowired
    private Converter<String, List<GatewayFlowRuleEntity>> converter;

    @Override
    public List<GatewayFlowRuleEntity> getRules(String app) throws Exception {
        String rules = configService.getConfig(app + NacosConfigUtil.GATEWAY_FLOW_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, 3000);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }
}
