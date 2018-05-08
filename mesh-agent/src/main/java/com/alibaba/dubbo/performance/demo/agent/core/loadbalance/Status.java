package com.alibaba.dubbo.performance.demo.agent.core.loadbalance;

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
