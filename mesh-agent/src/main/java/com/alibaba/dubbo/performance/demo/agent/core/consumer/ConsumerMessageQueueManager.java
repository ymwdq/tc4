package com.alibaba.dubbo.performance.demo.agent.core.consumer;

import com.alibaba.dubbo.performance.demo.agent.core.consumer.springhttp.Task;
import com.alibaba.dubbo.performance.demo.agent.message.MessageQueueImpl;
import com.alibaba.dubbo.performance.demo.agent.message.MessageQueueSafeImpl;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.dubbo.performance.demo.agent.message.model.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ConsumerMessageQueueManager {
    private static volatile MessageQueue sendQueue;
    private static volatile MessageQueue recvQueue;
    private static AgentRecvServiceImpl agentRecvService;
    private Logger logger = LoggerFactory.getLogger(ConsumerMessageQueueManager.class);

    public static MessageQueue getSendQueue() {
        return sendQueue;
    }

    public static MessageQueue getRecvQueue() {
        return recvQueue;
    }

    public synchronized static void offerSendQueue(Message msg, Task task) {
        sendQueue.offer(msg);
        agentRecvService.registerTask(task, msg);
    }

    public static void offerRecvQueue(Message msg) {
        recvQueue.offer(msg);
    }

    static {
        recvQueue = new MessageQueueSafeImpl();
        sendQueue = new MessageQueueImpl();
        agentRecvService = new AgentRecvServiceImpl();
        agentRecvService.setRecvQueue(recvQueue);
        agentRecvService.start();
    }
}
