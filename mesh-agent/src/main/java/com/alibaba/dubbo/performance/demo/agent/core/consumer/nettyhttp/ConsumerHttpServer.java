//package com.alibaba.dubbo.performance.demo.agent.core.consumer.nettyhttp;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//
//public final class ConsumerHttpServer {
//
//    private final int PORT;
//
//    public ConsumerHttpServer(int port) {
//        PORT = port;
//    }
//
//    public void start() throws Exception {
//        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            ServerBootstrap b = new ServerBootstrap();
//            b.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
//                    .childHandler(new ConsumerHttpServerInitializer());
//            Channel ch = b.bind(PORT).sync().channel();
//            System.err.println("Open your web browser and navigate to http://127.0.0.1:" + PORT + '/');
//            ch.closeFuture().sync();
//        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }
//
//}
