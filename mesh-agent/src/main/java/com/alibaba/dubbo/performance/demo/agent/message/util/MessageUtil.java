package com.alibaba.dubbo.performance.demo.agent.message.util;

import com.alibaba.dubbo.performance.demo.agent.message.MessageBucket;
import com.alibaba.dubbo.performance.demo.agent.message.MessageImpl;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;

/**
 * utils class
 */
public class MessageUtil {
    /**
     *
     * @param data
     * @param start
     * @param end
     * @return
     */
    public static Message decode(byte[] data, int start, int end) {
        return null;
    }

    public static byte[] encode(Message msg) {
        return null;
    }

    public static String msgToString(Message msg) {
        return JSON.toJSONString(msg);
    }

    public static MessageImpl stringToMsg(String data) {
        return JSON.parseObject(data, MessageImpl.class);
    }

    public static String messageBucketEncode(MessageBucket messageBucket) {
        return JSON.toJSONString(messageBucket);
    }

    public static MessageBucket messageBucketDecode(String data) {
        return JSON.parseObject(data, MessageBucket.class);
    }
}
