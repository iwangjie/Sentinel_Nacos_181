package com.alibaba.csp.sentinel.dashboard.rule.nacos.degrade;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("degradeRuleNacosProvider")
public class DegradeRuleNacosProvider extends AbstractNacosProvider<DegradeRuleEntity> {


    protected DegradeRuleNacosProvider(ConfigService configService, Converter<String, List<DegradeRuleEntity>> decoder) {
        super(configService, decoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.DEGRADE;
    }
}
