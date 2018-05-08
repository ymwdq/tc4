package com.alibaba.dubbo.performance.demo.agent.core.loadbalance;

import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;

/**
 * 
 */
public interface Status {
    int getSendQueueLen();
    int getRecvQueueLen();
    String cpuInfo();
    String memInfo();
    String diskIoInfo();
    String netIoInfo();
    String getHost();
    String getPort();
}
