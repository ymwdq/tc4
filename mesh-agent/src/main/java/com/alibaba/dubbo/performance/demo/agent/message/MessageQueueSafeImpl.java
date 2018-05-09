package com.alibaba.dubbo.performance.demo.agent.message;

import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.dubbo.performance.demo.agent.message.model.MessageQueue;

import java.util.LinkedList;

/**
 * not thread safe
 */
public class MessageQueueSafeImpl implements MessageQueue {
    private LinkedList<Message> queue = new LinkedList<>();

    @Override
    public synchronized void offer(Message message) {
        queue.offer(message);
    }

    @Override
    public synchronized Message poll() {
        Message message;
        if (size() == 0) {
            return null;
        }
        message = queue.poll();
        return message;
    }

    @Override
    public int size() {
        return queue.size();
    }
}
