package com.alibaba.csp.sentinel.dashboard.rule.nacos.param;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.param.define.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("paramFlowRuleNacosPublisher")
public class ParamFlowRuleNacosPublisher
        extends AbstractNacosPublisher<ParamFlowRuleEntity> {


    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<List<ParamFlowRuleCorrectEntity>, String> converter;

    @Autowired
    public ParamFlowRuleNacosPublisher(ConfigService configService,
                                       Converter<List<ParamFlowRuleEntity>, String> encoder) {
        super(configService, encoder);
    }
    @Override
    public void publish(String app, List<ParamFlowRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        rules.forEach(e -> e.setApp(app));

        //  转换
        List<ParamFlowRuleCorrectEntity> list = rules.stream().map(rule -> {
            ParamFlowRuleCorrectEntity entity = new ParamFlowRuleCorrectEntity();
            BeanUtils.copyProperties(rule, entity);
            return entity;
        }).collect(Collectors.toList());

        configService.publishConfig(app + NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, converter.convert(list));
    }


    @Override protected DataIdPostfix postfix() { return DataIdPostfix.PARAM; }
}
