package com.alibaba.csp.sentinel.dashboard.rule.nacos.gateway.api;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
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
 * 拉取Nacos中存储的网关分组管理规则配置信息
 *
 * @author 星空流年
 */
@Component("gatewayApiNacosProvider")
public class GatewayApiNacosProvider extends AbstractNacosProvider<ApiDefinitionEntity> {

    protected GatewayApiNacosProvider(ConfigService configService, Converter<String, List<ApiDefinitionEntity>> decoder) {
        super(configService, decoder);
    }

    @Override
    protected DataIdPostfix postfix() {
        return DataIdPostfix.GW_API;
    }
}
