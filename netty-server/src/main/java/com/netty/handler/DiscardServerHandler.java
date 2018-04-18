package com.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * 处理服务端的channel
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到连接:"+ctx.channel().remoteAddress());
//        ByteBuf in = (ByteBuf) msg;
//        try {
//        ReferenceCountUtil.release(msg);
//            System.out.println("收到数据:"+in.toString(CharsetUtil.UTF_8));
//            ctx.write(in.toString(CharsetUtil.UTF_8)+"too");
//            ctx.flush();
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
        try {
            ReferenceCountUtil.retain(msg);
            System.out.println("收到数据:"+((ByteBuf) msg).toString(CharsetUtil.UTF_8));
            Thread.sleep(5000);
            System.out.flush();
            ctx.write(msg);
            ctx.flush();
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //出现异常，关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
