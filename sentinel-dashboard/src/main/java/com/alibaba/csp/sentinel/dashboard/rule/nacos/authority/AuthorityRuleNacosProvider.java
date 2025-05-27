package com.alibaba.csp.sentinel.dashboard.rule.nacos.authority;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.authority.define.AuthorityRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 星空流年
 */
@Component("authorityRuleNacosProvider")
public class AuthorityRuleNacosProvider extends AbstractNacosProvider<AuthorityRuleEntity> {

    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<String, List<AuthorityRuleCorrectEntity>> converter;


    protected AuthorityRuleNacosProvider(ConfigService configService, Converter<String, List<AuthorityRuleEntity>> decoder) {
        super(configService, decoder);
    }

    @Override
    public List<AuthorityRuleEntity> getRules(String appName) throws Exception {
        String rules = configService.getConfig(appName + NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, 3000);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        List<AuthorityRuleCorrectEntity> entityList = converter.convert(rules);
        entityList.forEach(e -> e.setApp(appName));
        return entityList.stream().map(rule -> {
            AuthorityRule AuthorityRule = new AuthorityRule();
            BeanUtils.copyProperties(rule, AuthorityRule);
            AuthorityRuleEntity entity = AuthorityRuleEntity.fromAuthorityRule(rule.getApp(), rule.getIp(), rule.getPort(), AuthorityRule);
            entity.setId(rule.getId());
            entity.setGmtCreate(rule.getGmtCreate());
            return entity;
        }).collect(Collectors.toList());

    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.AUTHORITY;
    }
}
