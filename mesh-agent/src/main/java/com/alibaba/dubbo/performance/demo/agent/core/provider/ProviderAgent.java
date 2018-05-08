package com.alibaba.dubbo.performance.demo.agent.core.provider;

import com.alibaba.dubbo.performance.demo.agent.core.loadbalance.Status;

public interface ProviderAgent {
    Status getStatus();
    void register();
    void startService();
}
