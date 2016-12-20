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
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/12/16.
 */
@Slf4j
public class TCPRemoteClient implements RemoteClient {

    private Channel channel;
    EventLoopGroup group = new NioEventLoopGroup(1, new StatThreadFactory());

    private Bootstrap b;
    private TCPClientHandler clientHandler = new TCPClientHandler(this);

    private ServiceDetector serviceDetector;

    public TCPRemoteClient(ServiceDetector serviceDetector) {
        this.serviceDetector = serviceDetector;
    }

    public void startClient(RegisterHost registerHost) {
        log.info("startClient...");
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
                            p.addLast(new ProtobufVarint32LengthFieldPrepender());
                            p.addLast(new ProtobufEncoder());
                            p.addLast(clientHandler);
                        }
                    });
            doConnect(registerHost);
        } finally {
            // Shut down the event loop to terminate all threads.
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    group.shutdownGracefully();
                }
            }));
//            group.shutdownGracefully();
        }
    }

    private void doConnect(RegisterHost registerHost) {
        try {
            ChannelFuture f = b.connect(registerHost.getRemoteHostIP(), registerHost.getRemoteHostPort()).sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()) {//if is not successful, reconnect
                        future.channel().close();
                        f.channel().eventLoop().schedule(() -> doConnect(serviceDetector.discoverRemoteHost()), 1, TimeUnit.SECONDS);
                    }
                    channel = f.channel();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void heartbeat() {

    }

    public void reConnect() {
        RegisterHost registerHost = serviceDetector.discoverRemoteHost();

        doConnect(registerHost);
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
                voBuilder.setInvokeFailureTimes(data.getInvokeFailureTimes().get());
                voBuilder.setInvokeMaxCost(data.getInvokeMaxCost());
                voBuilder.setInvokeMinCost(data.getInvokeMinCost());
                voBuilder.setInvokeTimes(data.getInvokeTimes().get());
                voBuilder.setInvokeTotalCost(data.getInvokeTotalCost().get());
                voBuilder.setReportTimestamp(data.getReportTimestamp());
                builder.addStatVOList(voBuilder.build());
                voBuilder.clear();
            }
            channel.writeAndFlush(builder.build());
        }catch (Exception e) {
            log.error("report data fails", e);
            reConnect();
        }
    }
}
