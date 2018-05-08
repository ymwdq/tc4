package com.alibaba.dubbo.performance.demo.agent.core.consumer;

import com.alibaba.dubbo.performance.demo.agent.message.*;
import com.alibaba.dubbo.performance.demo.agent.message.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class ConsumerHttpHandler extends SimpleChannelInboundHandler<Object> {
    private HttpRequest request;
    private ByteBuf content;
    /**
     * Buffer that stores the response content
     */
    private final StringBuilder buf = new StringBuilder();
    private ChannelHandlerContext ctx;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        this.ctx = ctx;
        if (msg instanceof FullHttpRequest) {
            boolean keepAlive;
            FullHttpRequest fullHttpRequest = (FullHttpRequest)msg;
            // 通过 HTTP 协议头，区分客户端还是外部 service 请求
            if (fullHttpRequest.headers().contains("agent")) {
                dealWithAgentRequest(ctx, fullHttpRequest);
            } else {
                dealWithServiceRequest(ctx, fullHttpRequest);
            }
        }
    }

    private void writeResponse(String body, boolean keepAlive) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK,
                Unpooled.copiedBuffer(body.getBytes()));
        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        System.out.println(response.toString());
        ctx.writeAndFlush(response);
    }

    private void dealWithServiceRequest(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        boolean keepAlive;
        System.out.println("req: " + fullHttpRequest.toString());
        String body = fullHttpRequest.content().toString(CharsetUtil.UTF_8);
        String[] kvs = body.split("&");
        String parameter = kvs[3].split("=")[1].hashCode() + "";
        System.out.println("=== parameter === : " + parameter);
        keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
        System.out.println("=== keep alive === " + keepAlive);
        System.out.println("=== ctx hashcode === " + ctx.hashCode());
        Message agentMessage = new MessageImpl(String.valueOf(ctx.hashCode()), parameter);
        ThreadLocal<ConsumerMessageBucketQueueManager.ThreadMessageBucketProxy> messageBucketPoolThreadLocal = ConsumerMessageBucketQueueManager.ThreadLocalMessagePool.getLocal();
        MessageBucket bucket = messageBucketPoolThreadLocal.get().getBucket();
        bucket.addMessage(agentMessage);
        if (bucket.full()) {
            ConsumerMessageBucketQueueManager.getMessageBucketQueue().offer(bucket);
            messageBucketPoolThreadLocal.get().refresh();
        }
    }

    private void dealWithAgentRequest(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

