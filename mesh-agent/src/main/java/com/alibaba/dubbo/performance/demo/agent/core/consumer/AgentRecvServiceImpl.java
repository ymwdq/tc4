package com.alibaba.dubbo.performance.demo.agent.core.consumer;

import com.alibaba.dubbo.performance.demo.agent.core.consumer.model.AgentRecvService;
import com.alibaba.dubbo.performance.demo.agent.core.consumer.springhttp.IdGenerator;
import com.alibaba.dubbo.performance.demo.agent.core.consumer.springhttp.Task;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.dubbo.performance.demo.agent.message.model.MessageQueue;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

public class AgentRecvServiceImpl implements AgentRecvService {
    private MessageQueue recvQueue;
    private TaskMessageMap map = new TaskMessageMap();
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AgentRecvServiceImpl.class);
    public void setRecvQueue(MessageQueue messageQueue) {
        this.recvQueue = messageQueue;
    }

    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (recvQueue.size() != 0) {
                        Message msg = recvQueue.poll();
                        logger.info("msg: " + msg.getId() + ":" + msg.getBody());
                        Task task = map.get(msg);
                        logger.info("task" + task);
//                        task.setResult(JSON.parseObject((byte[])msg.getBody(), Integer.class));
                        task.setResult(Integer.valueOf((String)msg.getBody()));
                        IdGenerator.freeId(msg.getId());
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void registerHandler(ChannelInboundHandlerAdapter handlerAdapter) {

    }

    @Override
    public synchronized void registerTask(Task task, Message msg) {
        map.put(task, msg);

    }

    @Override
    public void registerDeferredResult(DeferredResult<Integer> result, String parameter) {

    }

    /**
     * not thread safe
     */
    public static class TaskMessageMap {

        private Task[] taskMap = new Task[3000];

        public void put(Task task, Message msg) {
            taskMap[msg.getId()] = task;
        }

        public Task get(Message msg) {
            Task r;
            r = taskMap[msg.getId()];
            clear(msg);
            return r;
        }

        public void clear(Message msg) {
            taskMap[msg.getId()] = null;
        }


    }
}