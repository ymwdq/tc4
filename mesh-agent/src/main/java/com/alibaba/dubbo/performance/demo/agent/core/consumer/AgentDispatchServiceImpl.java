package com.alibaba.dubbo.performance.demo.agent.core.consumer;


import com.alibaba.dubbo.performance.demo.agent.core.consumer.model.AgentDispatchService;
import com.alibaba.dubbo.performance.demo.agent.core.loadbalance.LoadBalanceStrategy;
import com.alibaba.dubbo.performance.demo.agent.message.MessageBucketQueue;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.dubbo.performance.demo.agent.message.model.MessageQueue;
import com.alibaba.dubbo.performance.demo.agent.message.util.MessageUtil;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class AgentDispatchServiceImpl implements AgentDispatchService {
    private OkHttpClient httpClient = new OkHttpClient();
    private EtcdRegistry registry;
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private Random random = new Random();
    private MessageQueue sendQueue;
    private LoadBalanceStrategy loadBalanceStrategy;

    public AgentDispatchServiceImpl(EtcdRegistry registry, LoadBalanceStrategy loadBalanceStrategy) {
        this.registry = registry;
        this.loadBalanceStrategy = loadBalanceStrategy;
    }

    public AgentDispatchServiceImpl(EtcdRegistry registry, MessageQueue sendQueue, LoadBalanceStrategy loadBalanceStrategy) {
        this.registry = registry;
        this.loadBalanceStrategy = loadBalanceStrategy;
        this.sendQueue = sendQueue;
    }

    @Override
    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @Override
    public void setLoadbalanceStrategy(LoadBalanceStrategy strategy) {
        this.loadBalanceStrategy = strategy;
    }

    @Override
    public void setBucketQueue(MessageBucketQueue messageBucketQueue) {

    }

    private void send(String parameter) throws Exception {
        if (null == endpoints){
            synchronized (lock){
                if (null == endpoints){
                    endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                }
            }
        }

        // 简单的负载均衡，随机取一个
        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));

        String url =  "http://" + endpoint.getHost() + ":" + endpoint.getPort();

        RequestBody requestBody = new FormBody.Builder()
                .add("parameter",parameter)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message sendMsg = sendQueue.poll();
                    try {
                        // to do
                        if (sendMsg == null) Thread.sleep(100);
                        else send(MessageUtil.msgToString(sendMsg));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
