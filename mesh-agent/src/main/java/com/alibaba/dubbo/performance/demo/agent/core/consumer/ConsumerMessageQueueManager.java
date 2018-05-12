package com.alibaba.dubbo.performance.demo.agent.core.consumer;

import com.alibaba.dubbo.performance.demo.agent.core.consumer.model.AgentDispatchService;
import com.alibaba.dubbo.performance.demo.agent.core.consumer.springhttp.Task;
import com.alibaba.dubbo.performance.demo.agent.message.MessageQueueImpl;
import com.alibaba.dubbo.performance.demo.agent.message.MessageQueueSafeImpl;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.dubbo.performance.demo.agent.message.model.MessageQueue;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ConsumerMessageQueueManager {
    private volatile MessageQueue sendQueue;
    private volatile MessageQueue recvQueue;
    private AgentRecvServiceImpl agentRecvService;
    private AgentDispatchService agentDispatchService;
    private Logger logger = LoggerFactory.getLogger(ConsumerMessageQueueManager.class);


    public ConsumerMessageQueueManager(IRegistry registry) {
        recvQueue = new MessageQueueSafeImpl();
        sendQueue = new MessageQueueImpl();
        agentRecvService = new AgentRecvServiceImpl();
        agentRecvService.setRecvQueue(recvQueue);
        agentRecvService.start();
        agentDispatchService = new AgentDispatchServiceImpl(registry);
        agentDispatchService.setSendQueue(sendQueue);
        agentDispatchService.start();
    }
    public MessageQueue getSendQueue() {
        return sendQueue;
    }

    public MessageQueue getRecvQueue() {
        return recvQueue;
    }

    public synchronized void offerSendQueue(Message msg, Task task) {
        sendQueue.offer(msg);
        agentRecvService.registerTask(task, msg);
    }

    public void offerRecvQueue(Message msg) {
        recvQueue.offer(msg);
    }


}
