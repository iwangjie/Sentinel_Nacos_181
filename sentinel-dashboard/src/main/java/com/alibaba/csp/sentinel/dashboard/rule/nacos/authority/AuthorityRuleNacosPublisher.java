package com.alibaba.csp.sentinel.dashboard.rule.nacos.authority;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("authorityRuleNacosPublisher")
public class AuthorityRuleNacosPublisher
        extends AbstractNacosPublisher<AuthorityRuleEntity> {

    @Autowired
    public AuthorityRuleNacosPublisher(ConfigService configService,
                                       Converter<List<AuthorityRuleEntity>, String> encoder) {
        super(configService, encoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.AUTHORITY;
    }
}
