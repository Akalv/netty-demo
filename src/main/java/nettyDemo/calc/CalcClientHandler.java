package nettyDemo.calc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import nettyDemo.handler.CalcBean;
import nettyDemo.handler.CalcResponse;

import java.util.Scanner;

public class CalcClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("请输入计算公式：");
//        String body = new Scanner(System.in).next();
//        ByteBuf buffer = Unpooled.buffer();
//        buffer.writeBytes(body.getBytes());
//        ctx.writeAndFlush(buffer);

        // 循环200次，将计算公式发送给服务器，模拟TCP拆包装包问题
//        String req = "666+666" + System.lineSeparator();
//        for (int i = 0; i < 200; i++) {
//            ByteBuf byteBuf = Unpooled.buffer(req.length());
//            byteBuf.writeBytes(req.getBytes());
//            ctx.channel().writeAndFlush(byteBuf);
//        }

        CalcBean calcBean = new CalcBean();
        System.out.println("请输入第一个数字：");
        Scanner scanner = new Scanner(System.in);
        calcBean.setNum1(scanner.nextInt());
        System.out.println("请输入计算符号：");
        calcBean.setSymbol(scanner.next());
        System.out.println("请输入第二个数字：");
        calcBean.setNum2(scanner.nextInt());
        ctx.writeAndFlush(calcBean);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] data = new byte[buf.readableBytes()];
//        buf.readBytes(data);
//        String body = new String(data, "utf-8");

        CalcResponse calcResponse = (CalcResponse) msg;

        System.out.println("接收到的计算结果是：" + calcResponse.getResult());

        // 测试粘包拆包时，将以下注释起来，避免阻塞
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
