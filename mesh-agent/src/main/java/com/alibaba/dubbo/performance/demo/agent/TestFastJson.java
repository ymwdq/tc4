package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.message.MessageImpl;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.dubbo.performance.demo.agent.message.util.MessageUtil;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by ym on 18-5-12.
 */
public class TestFastJson {
    public static void main(String[] args) {
//        MessageImpl msg = new MessageImpl(1, "test");
//        String msgStr = MessageUtil.msgToString(msg);
//        System.out.println(msgStr);
//        MessageImpl rMsg = MessageUtil.stringToMsg(msgStr);
//        rMsg.setBody(3);
//        String rStr = MessageUtil.msgToString(rMsg);
//        MessageImpl recvMsg = MessageUtil.stringToMsg(rStr);
//        System.out.println(recvMsg.getId());
//        System.out.println(recvMsg.getBody() instanceof String);
//        System.out.println(recvMsg.getBody());
        ArrayBlockingQueue<Message> blockingQueue = new ArrayBlockingQueue<Message>(10);
        Message msg = null;
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                blockingQueue.put(new MessageImpl(1, "test"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            msg = blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(msg);

    }
}
