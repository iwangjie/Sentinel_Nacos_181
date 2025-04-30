package com.alibaba.csp.sentinel.dashboard.rule.nacos.param;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("paramFlowRuleNacosProvider")
public class ParamFlowRuleNacosProvider extends AbstractNacosProvider<ParamFlowRuleEntity> {
    protected ParamFlowRuleNacosProvider(ConfigService configService, Converter<String, List<ParamFlowRuleEntity>> decoder) {
        super(configService, decoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.PARAM;
    }
}
