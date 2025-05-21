/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.discovery;

import com.alibaba.csp.sentinel.util.AssertUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author leyou
 */
@Component
public class SimpleMachineDiscovery implements MachineDiscovery, InitializingBean {

    private final ConcurrentMap<String, AppInfo> apps = new ConcurrentHashMap<>();

    @Override
    public long addMachine(MachineInfo machineInfo) {
        AssertUtil.notNull(machineInfo, "machineInfo cannot be null");
        AppInfo appInfo = apps.computeIfAbsent(machineInfo.getApp(), o -> new AppInfo(machineInfo.getApp(), machineInfo.getAppType()));
        appInfo.addMachine(machineInfo);
        return 1;
    }

    @Override
    public boolean removeMachine(String app, String ip, int port) {
        AssertUtil.assertNotBlank(app, "app name cannot be blank");
        AppInfo appInfo = apps.get(app);
        if (appInfo != null) {
            return appInfo.removeMachine(ip, port);
        }
        return false;
    }

    @Override
    public List<String> getAppNames() {
        return new ArrayList<>(apps.keySet());
    }

    @Override
    public AppInfo getDetailApp(String app) {
        AssertUtil.assertNotBlank(app, "app name cannot be blank");
        return apps.get(app);
    }

    @Override
    public Set<AppInfo> getBriefApps() {
        return new HashSet<>(apps.values());
    }

    @Override
    public void removeApp(String app) {
        AssertUtil.assertNotBlank(app, "app name cannot be blank");
        apps.remove(app);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建定时任务清理超过3小时未上报心跳的客户端
        Thread cleanTask = new Thread(() -> {
            while (true) {
                try {
                    // 每隔30分钟执行一次清理
                    Thread.sleep(30 * 60 * 1000);

                    // 当前时间
                    long currentTime = System.currentTimeMillis();
                    // 1小时的毫秒数
                    long threeHoursInMs = 1 * 60 * 60 * 1000;

                    // 需要移除的机器信息列表
                    List<MachineInfo> toRemoveMachines = new ArrayList<>();

                    // 遍历所有应用
                    for (AppInfo appInfo : apps.values()) {
                        for (MachineInfo machineInfo : appInfo.getMachines()) {
                            // 检查最后心跳时间是否超过3小时
                            if (currentTime - machineInfo.getLastHeartbeat() > threeHoursInMs) {
                                toRemoveMachines.add(machineInfo);
                            }
                        }
                    }

                    // 移除过期的机器实例
                    for (MachineInfo machineInfo : toRemoveMachines) {
                        removeMachine(machineInfo.getApp(), machineInfo.getIp(), machineInfo.getPort());
                    }

                    if (!toRemoveMachines.isEmpty()) {
                        System.out.println(String.format("[Machine Discovery] Removed %d machines that have not reported heartbeat for more than 3 hours",
                                toRemoveMachines.size()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 设置为守护线程
        cleanTask.setDaemon(true);
        cleanTask.setName("sentinel-dashboard-machine-discovery-clean-task");
        cleanTask.start();
    }
}
