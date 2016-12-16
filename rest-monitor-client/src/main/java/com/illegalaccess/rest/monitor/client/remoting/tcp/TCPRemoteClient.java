package com.illegalaccess.rest.monitor.client.remoting.tcp;

import com.illegalaccess.rest.monitor.client.lb.RegisterHost;
import com.illegalaccess.rest.monitor.client.lb.ServiceDetector;
import com.illegalaccess.rest.monitor.client.remoting.RemoteClient;
import com.illegalaccess.rest.monitor.client.support.StatThreadFactory;
import com.illegalaccess.rest.monitor.client.vo.InvocationStatVO;
import com.illegalaccess.rest.monitor.client.vo.proto.InvocationStatListProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by Administrator on 2016/12/16.
 */
@Slf4j
public class TCPRemoteClient implements RemoteClient {

    private ChannelFuture f;
    private Bootstrap b;
    private ChannelHandlerContext ctx;
    private TCPClientHandler clientHandler = new TCPClientHandler();

    private ServiceDetector serviceDetector;

    public TCPRemoteClient(ServiceDetector serviceDetector) {
        this.serviceDetector = serviceDetector;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void startClient(RegisterHost registerHost) {
        EventLoopGroup group = new NioEventLoopGroup(1, new StatThreadFactory());
        try {
            b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LineBasedFrameDecoder(128));
                            p.addLast(new StringDecoder());
//                            p.addLast(new ProtobufVarint32FrameDecoder());
//                            p.addLast(new ProtobufDecoder(InvocationStatProto.InvocationStatVO.getDefaultInstance()));
                            p.addLast(new ProtobufEncoder());
                            p.addLast(clientHandler);
                        }
                    });
            connectAndWait(registerHost);
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public void heartbeat() {

    }

    public void reConnect() {
        RegisterHost registerHost = serviceDetector.discoverRemoteHost();

        connectAndWait(registerHost);
    }

    private void connectAndWait(RegisterHost registerHost) {
        // Start the client.
        try {
            f = b.connect(registerHost.getRemoteHostIP(), registerHost.getRemoteHostPort()).sync();

            log.info("client connect to host:{}, port:{}", registerHost.getRemoteHostIP(), registerHost.getRemoteHostPort());

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendData(List<InvocationStatVO> statData) {
        try{
            InvocationStatListProto.InvocationStatListVO.Builder builder =
                    InvocationStatListProto.InvocationStatListVO.newBuilder();
            InvocationStatListProto.InvocationStatVO.Builder voBuilder =
                    InvocationStatListProto.InvocationStatVO.newBuilder();
            for (InvocationStatVO data : statData) {
                voBuilder.setApp(data.getApp());
                voBuilder.setHost(data.getHost());
                voBuilder.setMethodName(data.getMethodName());
                voBuilder.setInvokeFailureTimes(data.getInvokeFailureTimes());
                voBuilder.setInvokeMaxCost(data.getInvokeMaxCost());
                voBuilder.setInvokeMinCost(data.getInvokeMinCost());
                voBuilder.setInvokeTimes(data.getInvokeTimes());
                voBuilder.setInvokeTotalCost(data.getInvokeTotalCost());
                voBuilder.setReportTimestamp(data.getReportTimestamp());
                builder.addStatVOList(voBuilder.build());
                voBuilder.clear();
            }
            ctx.writeAndFlush(builder.build());
        }catch (Exception e) {
            log.error("report data fails", e);
            reConnect();
        }
    }
}
