package com.alibaba.csp.sentinel.dashboard.rule.nacos.system;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 星空流年
 */
@Component("systemRuleNacosProvider")
public class SystemRuleNacosProvider extends AbstractNacosProvider<SystemRuleEntity> {

    protected SystemRuleNacosProvider(ConfigService configService, Converter<String, List<SystemRuleEntity>> decoder) {
        super(configService, decoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.SYSTEM;
    }
}
