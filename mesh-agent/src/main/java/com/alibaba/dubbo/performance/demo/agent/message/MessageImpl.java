package com.alibaba.dubbo.performance.demo.agent.message;

import com.alibaba.dubbo.performance.demo.agent.message.model.Message;

/**
 * 用于 agent 之间传送消息
 */
public class MessageImpl implements Message {
    private String id;
    private String body;

    public MessageImpl(String id, String body) {
        this.id = id;
        this.body = body;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getBody() {
        return body;
    }


    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            return this.id.equals(((Message)obj).getId()) && this.body.equals(((Message)obj).getBody());
        }
        return false;
    }

    @Override
    public byte[] getBinaryBody() {
        return this.body.getBytes();
    }
}
