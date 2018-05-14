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
    private static final int PORT = 20000;
    private static final String serviceName = "consumer";
    private static ConsumerMessageQueueManager messageManager;

    private static long totalTime = 0;
    private static long cnt = 1;
    private static long avgTime = 0;

    @RequestMapping(value = "")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception {
        long reqTime = System.currentTimeMillis();
//        logger.info("get req: " + reqTime);
//        logger.info(parameter);
        DeferredResult<Integer> result = new DeferredResult<>();
        Task task = new Task(result);
        consumer(task, parameter);
        long afterTime = System.currentTimeMillis();
//        logger.info("after cosumer time: " + afterTime);
//        totalTime += (afterTime - reqTime);
//        logger.info("total time" + totalTime);
        return result;
    }

//    @RequestMapping(value = "")
//    public Object invoke() throws Exception {
//        logger.info("get req");
//        String parameter = "haha";
//        logger.info(parameter);
//        DeferredResult<Integer> result = new DeferredResult<>();
//        Task task = new Task(result);
//        consumer(task, parameter);
//        return result;
//    }
//



    public void consumer(Task task, String parameter) throws Exception {
        logger.info("consumer begin");
        Message msg = new MessageImpl(IdGenerator.getId(), parameter);
//        logger.info("" + msg.getId()+ ":" + msg.getBody());
        messageManager.offerSendQueue(msg, task);
    }

    @RequestMapping(value = "/agent")
    public Object getResult(@RequestParam("msg") String msgString) throws Exception {
        logger.info("get provider result");
//        logger.info("msgString: " + msgString);
        Message msg = MessageUtil.stringToMsg(msgString);
        logger.info("controller recv: " + msg.getId() + System.currentTimeMillis());
        messageManager.offerRecvQueue(msg);
        return "OK";
    }

    static {
        try {
            registry.register(serviceName, PORT);
            messageManager = new ConsumerMessageQueueManager(registry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

