package nettyDemo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcDecoder extends ByteToMessageDecoder {

    // head|xxxx|content|xxxxxx...
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 读取请求头，定义的是4个字节，不够的话直接返回等待下一次提交
        if (in.readableBytes() < 4) {
            return;
        }
        // readInt方法就是读取4个BYTE并转成INT
        int contentLength = in.readInt();
        // 如果剩余可读得到字节数比请求体长度小，还原bytebuf下表等待下一次请求报文
        if ( in.readableBytes() < contentLength) {
            in.readerIndex(in.readerIndex() - 4);
            return;
        }
        byte[] content = new byte[contentLength];
        in.readBytes(content);
        // 使用正则匹配，解析请求体并转换成对象
        Pattern pattern = Pattern.compile("^(\\d+)([+\\-*/])(\\d+)$");
        Matcher mather = pattern.matcher(new String(content));
        if (mather.find()) {
            int num1 = Integer.valueOf(mather.group(1));
            String symbol = mather.group(2);
            int num2 = Integer.valueOf(mather.group(3));
            CalcBean calcBean = new CalcBean();
            calcBean.setNum1(num1);
            calcBean.setNum2(num2);
            calcBean.setSymbol(symbol);
            System.out.println("接收到请求体：" + calcBean.toString());
            out.add(calcBean);
        }
    }
}
