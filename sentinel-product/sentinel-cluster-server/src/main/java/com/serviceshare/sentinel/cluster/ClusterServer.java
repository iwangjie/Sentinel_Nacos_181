package com.serviceshare.sentinel.cluster;

import com.alibaba.csp.sentinel.cluster.server.ClusterTokenServer;
import com.alibaba.csp.sentinel.cluster.server.SentinelDefaultTokenServer;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;


/**
 * 集群限流服务端
 *
 * @author wangjie
 */
public class ClusterServer {

    public static void main(String[] args) throws Exception {
        ClusterTokenServer tokenServer = new SentinelDefaultTokenServer();

        ClusterServerConfigManager.loadGlobalTransportConfig(new ServerTransportConfig()
                .setIdleSeconds(600)
                .setPort(18730));

        tokenServer.start();
    }
}
