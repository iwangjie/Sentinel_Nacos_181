package com.serviceshare.sentinel.cluster.init;

import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

public class TokenServerInitFunc implements InitFunc {
    // Nacos 分组ID
    private final String groupId = "SENTINEL_GROUP";
    // namespace对应的dataId
    private final String namespaceSetDataId = "cluster-server-namespace-set";
    // 服务器传输配置对应的dataId
    private final String serverTransportDataId = "cluster-server-transport-config";
    // Nacos命名空间ID - 使用您的dev命名空间ID
//    private final String nacosNamespaceIds = "dev,test";

    @Override
    public void init() throws Exception {

        // Nacos 服务地址
        String nacosAddress = System.getProperty("nacos.addr", "192.168.40.70:8848");
        String nacosNamespaceIds = System.getProperty("nacos.namespaces", "dev,test");
        String[] nsArr = nacosNamespaceIds.split(",");

        // 定义 Token Server 监听的命名空间集合
        Set<String> namespaces = new HashSet<>();
        for (String ns : nsArr) {
            namespaces.add(ns);
        }

        ClusterServerConfigManager.loadServerNamespaceSet(namespaces);


        // 设置 Nacos 连接参数
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosAddress);
//        properties.put(PropertyKeyConst.NAMESPACE, nacosNamespaceId);

        // 注册集群流控规则属性加载器 - 根据namespace创建数据源
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<FlowRule>> ds = new NacosDataSource<>(
                    properties, groupId,
                    namespace + "-flow-rules", // 对应您的应用如：agent-oms-webapp-flow-rules
                    source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                    })
            );
            return ds.getProperty();
        });

        // 注册参数流控规则属性加载器
        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<ParamFlowRule>> ds = new NacosDataSource<>(
                    properties, groupId,
                    namespace + "-param-rules",
                    source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {
                    })
            );
            return ds.getProperty();
        });

        // 注册服务器端命名空间集合数据源
        ReadableDataSource<String, Set<String>> namespaceDs = new NacosDataSource<>(
                properties, groupId,
                namespaceSetDataId,
                source -> JSON.parseObject(source, new TypeReference<Set<String>>() {
                })
        );
        ClusterServerConfigManager.registerNamespaceSetProperty(namespaceDs.getProperty());

        // 注册服务器端传输配置数据源
        ReadableDataSource<String, ServerTransportConfig> transportConfigDs = new NacosDataSource<>(
                properties, groupId,
                serverTransportDataId,
                source -> JSON.parseObject(source, new TypeReference<ServerTransportConfig>() {
                })
        );
        ClusterServerConfigManager.registerServerTransportProperty(transportConfigDs.getProperty());


        registerServerToNacos(nacosAddress, namespaces);

    }


    private void registerServerToNacos(String nacosAddress, Set<String> nsArr) throws Exception {
        for (String ns : nsArr) {
            // Create Nacos naming service
            Properties properties = new Properties();
            properties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosAddress);
            properties.setProperty(PropertyKeyConst.NAMESPACE, ns);
            NamingService namingService = NacosFactory.createNamingService(properties);

            // Create instance for registration
            Instance instance = new Instance();
            // Get the server's IP address (in production, use actual IP)
            instance.setIp(getLocalHostIP());
            // Get the port from the cluster server config
            instance.setPort(ClusterServerConfigManager.getPort());
            instance.setServiceName("sentinel-token-server");

            // Add metadata if needed
            instance.addMetadata("version", "1.0");
            instance.addMetadata("type", "sentinel-token-server");

            // Register the instance
            namingService.registerInstance("sentinel-token-server", instance);

            // Log the registration
            RecordLog.info("[NacosTokenServerInitFunc] Token server registered to Nacos as service: {}", "sentinel-token-server");
        }

    }

    private static String getLocalHostIP() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // 跳过未启用、回环接口或虚拟网卡
                if (networkInterface.isLoopback() || !networkInterface.isUp() || isVirtualInterface(networkInterface)) {
                    continue;
                }
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // 过滤掉回环地址和 IPv6 地址
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof java.net.Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果无法获取有效 IP，返回默认值或抛出异常
        return "127.0.0.1"; // 或者抛出自定义异常
    }

    private static boolean isVirtualInterface(NetworkInterface networkInterface) {
        // 检查网卡名称是否包含虚拟网卡的标志
        String name = networkInterface.getName().toLowerCase();
        return name.contains("virtual") || name.contains("vethernet") || name.contains("vmnet") || name.contains("utun") || name.contains("docker");
    }
}
