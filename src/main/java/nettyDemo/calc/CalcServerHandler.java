package nettyDemo.calc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import nettyDemo.handler.CalcBean;
import nettyDemo.handler.CalcResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有新的客户端连接至服务器");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CalcBean calcBean = (CalcBean) msg;
        System.out.println("接受到的请求：" + calcBean.toString());
        double result = 0;
        double num1 = calcBean.getNum1();
        double num2 = calcBean.getNum2();

        String symbol = calcBean.getSymbol();
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

//        ByteBuf response = Unpooled.buffer();
//        response.writeBytes((result + System.lineSeparator()).getBytes());
//
//        ctx.writeAndFlush(response);

        ctx.writeAndFlush(new CalcResponse(result));
    }
}
