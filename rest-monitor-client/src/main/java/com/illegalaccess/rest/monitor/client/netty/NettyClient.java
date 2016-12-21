package com.illegalaccess.rest.monitor.client.netty;

import com.illegalaccess.rest.monitor.client.netty.tcp.EchoClientHandler;
import com.illegalaccess.rest.monitor.client.netty.udp.EchoUDPClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Administrator on 2016/12/14.
 */
@Slf4j
public class NettyClient {

    static int port = 2112;

    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
        startUDPClient();
    }

    static void startUDPClient() throws InterruptedException, UnsupportedEncodingException {
        final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioDatagramChannel.class);
        bootstrap.group(nioEventLoopGroup);
        bootstrap.handler(new ChannelInitializer<NioDatagramChannel>() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
            }

            @Override
            protected void initChannel(NioDatagramChannel ch) throws Exception {
                ChannelPipeline cp = ch.pipeline();
                cp.addLast("framer", new MessageToMessageDecoder<DatagramPacket>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
                        out.add(msg.content().toString(Charset.forName("UTF-8")));
                    }
                }).addLast("handler", new EchoUDPClientHandler());
            }
        });
        // 监听端口
        ChannelFuture sync = bootstrap.bind(0).sync();
        Channel udpChannel = sync.channel();

        String data = "我是大好人啊";
        udpChannel.writeAndFlush(
                new DatagramPacket(
                        Unpooled.copiedBuffer(data.getBytes("UTF-8")),
                        new InetSocketAddress("127.0.0.1", port)
                ));

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                nioEventLoopGroup.shutdownGracefully();
            }
        }));

    }

    static void startTCPClient() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LineBasedFrameDecoder(1024));
                            p.addLast(new StringDecoder());
                            p.addLast(new EchoClientHandler());
                        }
                    });
            // Start the client.
            ChannelFuture f = b.connect("127.0.0.1", port).sync();

            log.info("client connect to host:{}, port:{}", "127.0.0.1", port);

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
