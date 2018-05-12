package com.alibaba.dubbo.performance.demo.agent.core.consumer.springhttp;

import com.alibaba.dubbo.performance.demo.agent.core.consumer.AgentDispatchServiceImpl;
import com.alibaba.dubbo.performance.demo.agent.core.consumer.ConsumerMessageQueueManager;
import com.alibaba.dubbo.performance.demo.agent.core.consumer.model.AgentDispatchService;
import com.alibaba.dubbo.performance.demo.agent.message.MessageImpl;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.dubbo.performance.demo.agent.message.util.MessageUtil;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;


@RestController
public class HelloController {

    private static Logger logger = LoggerFactory.getLogger(HelloController.class);

    private static IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private static AgentDispatchServiceImpl agentDispatchService = new AgentDispatchServiceImpl(registry);
    private static final int PORT = 8080;
    private static final String serviceName = "consumer";

    @RequestMapping(value = "/service")
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

    @RequestMapping(value = "/agent")
    public Object getResult(@RequestParam("msg") String msgString) throws Exception {
        logger.info("get provider result");
        Message msg = MessageUtil.stringToMsg(msgString);
        ConsumerMessageQueueManager.offerRecvQueue(msg);
        return "over";
    }

    static {
        try {
            registry.register(serviceName, PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        agentDispatchService.start();
    }


}

