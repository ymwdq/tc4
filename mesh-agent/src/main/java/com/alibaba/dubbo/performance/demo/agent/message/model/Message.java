package com.alibaba.dubbo.performance.demo.agent.message.model;

import io.netty.buffer.ByteBuf;

/**
 * Created by ym on 18-5-6.
 */
public interface Message {
    String getId();
    String getBody();
    byte[] getBinaryBody();
}
