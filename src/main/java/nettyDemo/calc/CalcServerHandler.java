package nettyDemo.calc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有新的客户端连接至服务器");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        String body = new String(data, "utf-8");
        System.out.println("接收到计算公式：" + body);

        // 只支持简单两个数字计算
        Pattern pattern = Pattern.compile("^(\\d+)([+\\-*/])(\\d+)$");
        Matcher matcher = pattern.matcher(body);
        double result = 0;
        if (matcher.find()) {
            double num1 = Double.valueOf(matcher.group(1));
            double num2 = Double.valueOf(matcher.group(3));
            String symbol = matcher.group(2);
            switch (symbol) {
                case "+":
                    result = num1 + num2;
                    break;
                case "_":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    result = num1 / num2;
                    break;
            }
        } else {
            System.out.println("计算公式不正确");
        }

        ByteBuf response = Unpooled.buffer();
        response.writeBytes((result + "").getBytes());

        ctx.writeAndFlush(response);
    }
}
