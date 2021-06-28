package nettyDemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {

    public static void main(String[] args) {
        // 处理客户端链接的线程池
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        // 处理IO的线程池
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {

                            // 自定义handle处理数据
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    // 当读取到客户端数据时，直接写入到客户端(使用telnet连接输入，是即时反馈的)
                                    ctx.write( msg);
                                    ctx.flush();
                                }
                            });
                        }
                    });
            ChannelFuture sync = bootstrap.bind(9999).sync();
            System.out.println("服务器启动成功");
            // 阻塞当前线程，直到服务器关闭
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
