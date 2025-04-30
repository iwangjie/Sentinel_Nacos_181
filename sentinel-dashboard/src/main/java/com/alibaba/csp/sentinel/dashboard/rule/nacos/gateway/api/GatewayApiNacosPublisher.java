package com.alibaba.csp.sentinel.dashboard.rule.nacos.gateway.api;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.common.DataIdPostfix;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("gatewayApiNacosPublisher")
public class GatewayApiNacosPublisher
        extends AbstractNacosPublisher<ApiDefinitionEntity> {

    @Autowired
    public GatewayApiNacosPublisher(ConfigService configService,
                                    Converter<List<ApiDefinitionEntity>, String> encoder) {
        super(configService, encoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.GW_API;
    }
}
