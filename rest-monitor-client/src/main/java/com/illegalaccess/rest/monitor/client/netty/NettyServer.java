package com.illegalaccess.rest.monitor.client.netty;

import com.illegalaccess.rest.monitor.client.netty.tcp.EchoServerHandler;
import com.illegalaccess.rest.monitor.client.netty.udp.EchoUDPSeverHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.concurrent.ThreadFactory;

/**
 * Created by Administrator on 2016/12/14.
 */
public class NettyServer {

    static int port = 2112;

    public static void main(String[] args) throws InterruptedException {
//        startUDPServer();
        startTCPServer();
    }

    static void startUDPServer() throws InterruptedException {
        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
        .channel(NioDatagramChannel.class)
        .handler(new EchoUDPSeverHandler());

        // 服务端监听在9999端口
        b.bind(port).sync().channel().closeFuture().await();
    }

    static void startUDTServer() throws Exception {
//        final ThreadFactory acceptFactory = new UtilThreadFactory("accept");
//        final ThreadFactory connectFactory = new UtilThreadFactory("connect");
//        final NioEventLoopGroup acceptGroup = new NioEventLoopGroup(1,
//                acceptFactory, NioUdtProvider.BYTE_PROVIDER);
//        final NioEventLoopGroup connectGroup = new NioEventLoopGroup(1,
//                connectFactory, NioUdtProvider.BYTE_PROVIDER);
//        // Configure the server.
//        try {
//            final ServerBootstrap boot = new ServerBootstrap();
//            boot.group(acceptGroup, connectGroup)
//                    .channelFactory(NioUdtProvider.BYTE_ACCEPTOR)
//                    .option(ChannelOption.SO_BACKLOG, 10)
////                    .handler(new LoggingHandler(LogLevel.INFO))
//                    .childHandler(new ChannelInitializer() {
//                        @Override
//                        public void initChannel(final UdtChannel ch)
//                                throws Exception {
//                            ch.pipeline().addLast(
//                                    new ModemServerHandler()
//                            );
//                        }
//                    });
//            // Start the server.
//            final ChannelFuture future = boot.bind(port).sync();
//            // Wait until the server socket is closed.
//            future.channel().closeFuture().sync();
//        } finally {
//            // Shut down all event loops to terminate all threads.
//            acceptGroup.shutdownGracefully();
//            connectGroup.shutdownGracefully();
//        }
    }

    static void startTCPServer() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LineBasedFrameDecoder(1024));
                            p.addLast(new StringDecoder());
                            p.addLast(new EchoServerHandler());
                        }
                    });

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            System.out.println("server bind part:" + port);

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
