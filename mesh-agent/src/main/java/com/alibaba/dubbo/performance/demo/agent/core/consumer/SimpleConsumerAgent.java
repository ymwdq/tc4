//package com.alibaba.dubbo.performance.demo.agent.core.consumer;
//
//import com.alibaba.dubbo.performance.demo.agent.core.consumer.model.ConsumerAgent;
//import com.alibaba.dubbo.performance.demo.agent.core.loadbalance.Status;
//import com.alibaba.dubbo.performance.demo.agent.message.MessageBucketQueue;
//import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SimpleConsumerAgent extends ConsumerAgent {
//    private EtcdRegistry etcdRegistry;
//    private final MessageBucketQueue messageBucketQueue = new MessageBucketQueue();
//    private final AgentDispatchServiceImpl consumerDispatchService;
//    private final int servicePort;
//    private final int agentPort;
//    private final String serviceName;
//
//    @Autowired
//    private ConsumerMessageQueueManager consumerMessageQueueManager;
//
//    public SimpleConsumerAgent(EtcdRegistry registry, int servicePort, int agentPort, String serviceName) {
//        this.etcdRegistry = registry;
//        this.consumerDispatchService = new AgentDispatchServiceImpl(registry, messageBucketQueue, null);
//        this.serviceName = serviceName;
//        this.servicePort = servicePort;
//        this.agentPort = agentPort;
//        register();
//    }
//
//
//    @Override
//    public Status getStatus() {
//        return null;
//    }
//
//    @Override
//    public void register() {
//        try {
//            etcdRegistry.register(serviceName, agentPort);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void start() {
//        try {
//            this.consumerDispatchService.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
