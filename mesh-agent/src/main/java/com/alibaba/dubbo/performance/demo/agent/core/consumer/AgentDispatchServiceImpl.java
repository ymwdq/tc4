package com.alibaba.dubbo.performance.demo.agent.core.consumer;


import com.alibaba.dubbo.performance.demo.agent.core.consumer.model.AgentDispatchService;
import com.alibaba.dubbo.performance.demo.agent.core.loadbalance.LoadBalanceStrategy;
import com.alibaba.dubbo.performance.demo.agent.message.MessageBucket;
import com.alibaba.dubbo.performance.demo.agent.message.MessageBucketQueue;
import com.alibaba.dubbo.performance.demo.agent.message.MessageUtil;
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
    private MessageBucketQueue messageBucketQueue;
    private LoadBalanceStrategy loadBalanceStrategy;

    public AgentDispatchServiceImpl(EtcdRegistry registry, MessageBucketQueue messageBucketQueue, LoadBalanceStrategy loadBalanceStrategy) {
        this.registry = registry;
        this.messageBucketQueue = messageBucketQueue;
        this.loadBalanceStrategy = loadBalanceStrategy;
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
        this.messageBucketQueue = messageBucketQueue;
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
                    MessageBucket messageBucket = messageBucketQueue.poll();
                    try {
                        if (messageBucket == null) Thread.sleep(100);
                        else send(MessageUtil.messageBucketEncode(messageBucket));
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
