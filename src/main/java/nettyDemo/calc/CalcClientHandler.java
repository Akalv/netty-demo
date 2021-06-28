package nettyDemo.calc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Scanner;

public class CalcClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("请输入计算公式：");
        String body = new Scanner(System.in).next();
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(body.getBytes());
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        String body = new String(data, "utf-8");
        System.out.println("接收到的计算结果是：" + body);

        System.out.println("是否要继续计算（Y/N）");
        String flag = new Scanner(System.in).next();
        if ("Y".equalsIgnoreCase(flag)) {
            channelActive(ctx);
        } else {
            System.out.println("客户端关闭中...");
            ctx.close();
        }
    }
}
