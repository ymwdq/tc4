//package com.alibaba.dubbo.performance.demo.agent.core.consumer.nettyhttp;
//
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.HttpRequestDecoder;
//import io.netty.handler.codec.http.HttpResponseEncoder;
//
//
//public class ConsumerHttpServerInitializer extends ChannelInitializer<SocketChannel> {
//    @Override
//    public void initChannel(SocketChannel ch) {
//        ChannelPipeline p = ch.pipeline();
//        p.addLast(new HttpRequestDecoder());
//        p.addLast(new HttpObjectAggregator(65536));
//        p.addLast(new HttpResponseEncoder());
//        p.addLast(new ConsumerHttpHandler());
//    }
//}