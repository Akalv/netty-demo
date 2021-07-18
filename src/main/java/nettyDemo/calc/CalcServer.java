package nettyDemo.calc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import nettyDemo.handler.CalcDecoder;
import nettyDemo.handler.HessianDecoder;
import nettyDemo.handler.HessianEncoder;

public class CalcServer {

    public void bind(int port) {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {

                            // 使用换行符来分割业务数据，解决拆包粘包问题
//                            channel.pipeline().addLast(new LineBasedFrameDecoder(1024));

                            // 自定义分隔符
//                            channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer("$".getBytes())));

                            // 固定长度分隔符
//                            channel.pipeline().addLast(new FixedLengthFrameDecoder(10));

                            // 使用自定义解码器
//                            channel.pipeline().addLast(new CalcDecoder());

                            // 使用JDK序列化编解码器
//                            channel.pipeline().addLast(new ObjectEncoder());
//                            channel.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingResolver(this.getClass().getClassLoader())));

                            // 使用自定义hessian编解码器
                            channel.pipeline().addLast(new HessianEncoder());
                            channel.pipeline().addLast(new HessianDecoder());
                            channel.pipeline().addLast(new CalcServerHandler());
                        }
                    });
            ChannelFuture sync = bootstrap.bind(port).sync();
            System.out.println("服务器启动成功");
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new CalcServer().bind(9999);
    }
}
