package com.alibaba.dubbo.performance.demo.agent.core.consumer.springhttp;

import com.alibaba.dubbo.performance.demo.agent.core.consumer.ConsumerMessageQueueManager;
import com.alibaba.dubbo.performance.demo.agent.message.MessageImpl;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;


@RestController
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);

//    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));

    @RequestMapping(value = "")
    public DeferredResult<Integer> invoke() throws Exception {
        logger.info("get req");
        String parameter = "haha";
        DeferredResult<Integer> result = new DeferredResult<>();
        Task task = new Task(result);
        consumer(task, parameter);
        return result;
    }



    public void consumer(Task task, String parameter) throws Exception {
        logger.info("consumer begin");
        Message msg = new MessageImpl(IdGenerator.getId(), parameter);
        ConsumerMessageQueueManager.offerSendQueue(msg, task);
    }
}

