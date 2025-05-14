package com.alibaba.csp.sentinel.dashboard.rule.nacos.authority;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.authority.define.AuthorityRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("authorityRuleNacosPublisher")
public class AuthorityRuleNacosPublisher
        extends AbstractNacosPublisher<AuthorityRuleEntity> {


    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<List<AuthorityRuleCorrectEntity>, String> converter;


    @Autowired
    public AuthorityRuleNacosPublisher(ConfigService configService,
                                       Converter<List<AuthorityRuleEntity>, String> encoder) {
        super(configService, encoder);
    }

    @Override
    public void publish(String app, List<AuthorityRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        rules.forEach(e -> e.setApp(app));

        //  转换
        List<AuthorityRuleCorrectEntity> list = rules.stream().map(rule -> {
            AuthorityRuleCorrectEntity entity = new AuthorityRuleCorrectEntity();
            BeanUtils.copyProperties(rule, entity);
            return entity;
        }).collect(Collectors.toList());

        configService.publishConfig(app + NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, converter.convert(list));
    }


    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.AUTHORITY;
    }
}
