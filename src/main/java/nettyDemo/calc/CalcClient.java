package nettyDemo.calc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import nettyDemo.handler.CalcMsgToByteEncoder;
import nettyDemo.handler.HessianDecoder;
import nettyDemo.handler.HessianEncoder;

public class CalcClient {

    public void connect(String host, int port) {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap().group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
//                            channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
//                            channel.pipeline().addLast(new CalcMsgToByteEncoder());
//                            // 使用JDK序列化编解码器
//                            channel.pipeline().addLast(new ObjectEncoder());
//                            channel.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingResolver(this.getClass().getClassLoader())));
//                            // 使用自定义hessian编解码器
                            channel.pipeline().addLast(new HessianEncoder());
                            channel.pipeline().addLast(new HessianDecoder());

                            channel.pipeline().addLast(new CalcClientHandler());

                        }
                    });
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new CalcClient().connect("127.0.0.1", 9999);
    }
}
