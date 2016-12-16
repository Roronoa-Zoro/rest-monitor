package com.illegalaccess.rest.monitor.core.server.tcp;

import com.illegalaccess.rest.monitor.client.support.StatThreadFactory;
import com.illegalaccess.rest.monitor.client.vo.proto.InvocationStatListProto;
import com.illegalaccess.rest.monitor.core.server.MonitorServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/12/16.
 */
@Service
public class TCPServer implements MonitorServer {

    @Value("${tcpServer.port}")
    private int port;

    public void startServer() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(0, new StatThreadFactory("TcpServer_Accept_Thread")); // (1)
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(10, 30, 3L,
                        TimeUnit.MINUTES,
                        new ArrayBlockingQueue<Runnable>(1000),
                        new StatThreadFactory("TcpServer_Worker_Thread"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(0, executor);
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ProtobufVarint32FrameDecoder());
                            p.addLast(new ProtobufDecoder(InvocationStatListProto.InvocationStatListVO.getDefaultInstance()));
                            p.addLast(new StringEncoder());
                            p.addLast(new StatDataTCPServerHandler());
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
