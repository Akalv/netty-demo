package nettyDemo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CalcMsgToByteEncoder extends MessageToByteEncoder<CalcBean> {
    @Override
    protected void encode(ChannelHandlerContext ctx, CalcBean msg, ByteBuf out) throws Exception {
        byte[] content = (msg.getNum1() + msg.getSymbol() + msg.getNum2()).getBytes();

        out.writeInt(content.length);

        out.writeBytes(content);
    }
}
