package com.alibaba.dubbo.performance.demo.agent.message;

import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.dubbo.performance.demo.agent.message.model.MessageQueue;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * not thread safe
 */
public class MessageQueueSafeImpl implements MessageQueue {
    private BlockingQueue<Message> queue = new ArrayBlockingQueue<>(512);

    @Override
    public void offer(Message message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message poll() {
        try {
            return this.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int size() {
        return queue.size();
    }
}
