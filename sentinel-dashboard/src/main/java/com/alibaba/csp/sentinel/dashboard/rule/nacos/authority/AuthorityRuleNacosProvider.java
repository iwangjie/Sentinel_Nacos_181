package com.alibaba.csp.sentinel.dashboard.rule.nacos.authority;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 星空流年
 */
@Component("authorityRuleNacosProvider")
public class AuthorityRuleNacosProvider extends AbstractNacosProvider<AuthorityRuleEntity> {

    protected AuthorityRuleNacosProvider(ConfigService configService, Converter<String, List<AuthorityRuleEntity>> decoder) {
        super(configService, decoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.AUTHORITY;
    }
}
