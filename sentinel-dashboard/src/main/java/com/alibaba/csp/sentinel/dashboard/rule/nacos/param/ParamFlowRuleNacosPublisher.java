package com.alibaba.csp.sentinel.dashboard.rule.nacos.param;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("paramFlowRuleNacosPublisher")
public class ParamFlowRuleNacosPublisher
        extends AbstractNacosPublisher<ParamFlowRuleEntity> {

    @Autowired
    public ParamFlowRuleNacosPublisher(ConfigService configService,
                                       Converter<List<ParamFlowRuleEntity>, String> encoder) {
        super(configService, encoder);
    }

    @Override protected DataIdPostfix postfix() { return DataIdPostfix.PARAM; }
}
