package com.alibaba.csp.sentinel.dashboard.rule.nacos.common;

public enum DataIdPostfix {

    FLOW("-flow-rules"),
    DEGRADE("-degrade-rules"),
    SYSTEM("-system-rules"),
    AUTHORITY("-authority-rules"),
    PARAM("-param-rules"),
    GW_FLOW("-gw-flow-rules"),
    GW_API("-gw-api-group-rules");

    private final String postfix;

    DataIdPostfix(String postfix) {
        this.postfix = postfix;
    }

    /** 组装完整 dataId */
    public String dataId(String app) {
        return app + postfix;
    }
}
