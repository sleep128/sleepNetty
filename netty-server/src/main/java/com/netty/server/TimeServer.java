package com.netty.server;

import com.netty.handler.DiscardServerHandler;
import com.netty.handler.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 启动DiscardServerHandler的服务
 */
public class TimeServer {
    private String host;
    private int port;

    public TimeServer(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void run() throws Exception{
        //用来接收进来的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();//(1)
        //用来处理已经被接收的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //ServerBootstrap是一个启动NIO服务的辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();//(2)
            bootstrap.group(bossGroup,workerGroup)
                    //指定NioServerSocketChannel类来举例说明一个新的channel如何接收连接
                    .channel(NioServerSocketChannel.class)//(3)
                    .childHandler(new ChannelInitializer<SocketChannel>() {//(4)
                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)//(5)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);//(6)
            //绑定端口，开始接收进来你的连接
            ChannelFuture f = bootstrap.bind(host,port).sync();//(7)
            //等待服务器关闭，这个例子不会发生，直接关闭
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        String host;
        int port;
        if(args.length==2){
            host = args[0];
            port = Integer.parseInt(args[1]);
        }else{
            host = "127.0.0.1";
            port = 8080;
        }
        System.out.println(String.format("sever start,host:%s,port:%d",host,port));
        new TimeServer(host,port).run();
    }
}
