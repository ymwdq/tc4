package com.alibaba.dubbo.performance.demo.agent.message;

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

    public static void writeToCtx(ChannelHandlerContext ctx, Message msg) {
        return;

    }

    public static String messageBucketEncode(MessageBucket messageBucket) {
        return JSON.toJSONString(messageBucket);
    }

    public static MessageBucket messageBucketDecode(String data) {
        return JSON.parseObject(data, MessageBucket.class);
    }
}
