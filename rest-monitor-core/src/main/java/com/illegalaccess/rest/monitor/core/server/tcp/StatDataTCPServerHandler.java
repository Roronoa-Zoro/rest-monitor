package com.illegalaccess.rest.monitor.core.server.tcp;

import com.illegalaccess.rest.monitor.client.vo.proto.InvocationStatListProto;
import com.illegalaccess.rest.monitor.core.queue.StatQueue;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by Jimmy Li on 2016/12/16.
 */
@Slf4j
public class StatDataTCPServerHandler extends ChannelInboundHandlerAdapter {

    private StatQueue statQueue;

    public StatDataTCPServerHandler(StatQueue statQueue) {
        this.statQueue = statQueue;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("server channel read...");
        try {
            InvocationStatListProto.InvocationStatListVO body = (InvocationStatListProto.InvocationStatListVO) msg;
            statQueue.offerMessage(body);
            log.info("server channel read msg:{}", body);
        }catch (Exception e) {
            e.printStackTrace();
        }

        String response = "server received";
//        ByteBuf resp = Unpooled.copiedBuffer(response.getBytes());
        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("server channel read complete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        log.error("server caught exception", cause);
        ctx.close();
    }
}
