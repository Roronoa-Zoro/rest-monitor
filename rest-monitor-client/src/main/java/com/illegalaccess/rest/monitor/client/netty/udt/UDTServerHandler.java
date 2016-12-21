package com.illegalaccess.rest.monitor.client.netty.udt;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2016/12/15.
 */
@Slf4j
public class UDTServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        log.info("server active");
        System.out.println("channelActive===================");
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        System.out.println("channelRead===================");
        ByteBuf buf = (ByteBuf) msg;
        try {
            StringBuilder sb=new StringBuilder();
            for (int i = 0; i < buf.capacity(); i ++) {
                byte b = buf.getByte(i);
                sb.append((char) b);
            }
            System.out.println(sb.toString());

        } catch(Exception e){
            e.printStackTrace();
        }
//        finally {
//            ReferenceCountUtil.release(msg);
//            msg.
//        }
        //ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final Throwable cause) {
        log.info("close the connection when an exception is raised", cause);
        ctx.close();
    }
}
