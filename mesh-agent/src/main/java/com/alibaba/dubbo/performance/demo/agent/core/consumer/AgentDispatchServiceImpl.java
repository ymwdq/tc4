package com.alibaba.dubbo.performance.demo.agent.core.consumer;


import com.alibaba.dubbo.performance.demo.agent.core.consumer.model.AgentDispatchService;
import com.alibaba.dubbo.performance.demo.agent.core.consumer.springhttp.HelloController;
import com.alibaba.dubbo.performance.demo.agent.core.loadbalance.LoadBalanceStrategy;
import com.alibaba.dubbo.performance.demo.agent.message.MessageBucketQueue;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.dubbo.performance.demo.agent.message.model.MessageQueue;
import com.alibaba.dubbo.performance.demo.agent.message.util.MessageUtil;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class AgentDispatchServiceImpl implements AgentDispatchService {
    private static Logger logger = LoggerFactory.getLogger(AgentDispatchServiceImpl.class);
    private OkHttpClient httpClient = new OkHttpClient();
    private IRegistry registry;
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private Random random = new Random();
    private MessageQueue sendQueue;
    private LoadBalanceStrategy loadBalanceStrategy;

    public AgentDispatchServiceImpl(IRegistry registry) {
        this.registry = registry;
        if (null == endpoints){
            synchronized (lock){
                if (null == endpoints){
                    try {
                        endpoints = registry.find("provider");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (Endpoint endpoint : endpoints) {
            System.out.println("end points" + endpoint.getHost());
        }

    }

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
    public void setSendQueue(MessageQueue sendQueue) {
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

//        for (Endpoint each : endpoints) {
//            logger.info(each.getHost());
//            logger.info("" + each.getPort());
//        }
        // 简单的负载均衡，随机取一个
        Endpoint endpoint = endpoints.get(random.nextInt(endpoints.size()));

        String url =  "http://" + endpoint.getHost() + ":" + endpoint.getPort() + "/agent";

        RequestBody requestBody = new FormBody.Builder()
                .add("msg",parameter)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        logger.info("send time" + System.currentTimeMillis());
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
//        try (Response response = httpClient.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("Unexpected code " + response);
//            }
//        }
    }

    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
//                    Message sendMsg = sendQueue.poll();
//                    try {
//                        send(MessageUtil.msgToString(sendMsg));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    try {
                        Message sendMsg = sendQueue.poll();
                        // to do
                        if (sendMsg == null) {
//                            logger.info("send queue empty");
//                            Thread.sleep(100);
                        } else {
                            logger.info("send queue size" + sendQueue.size());
//                            logger.info("msg send : " + sendMsg.getId());
                            logger.info("send msg " + sendMsg.getId() + " " + System.currentTimeMillis());
                            send(MessageUtil.msgToString(sendMsg));
                        }
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
