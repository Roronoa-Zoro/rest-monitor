package com.illegalaccess.rest.monitor.client.remoting.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2016/12/16.
 */
@Slf4j
public class TCPClientHandler extends ChannelInboundHandlerAdapter {

    private TCPRemoteClient remoteClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        remoteClient.setCtx(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        try {
            String body = (String) msg;
            log.info("client channel read msg:{}", body);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        log.error("client caught exception", cause);
        ctx.close();
    }
}
