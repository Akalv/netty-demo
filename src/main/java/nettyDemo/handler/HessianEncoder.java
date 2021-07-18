package nettyDemo.handler;

import com.caucho.hessian.io.Hessian2Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class HessianEncoder extends MessageToByteEncoder {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        int startIndex = out.writerIndex();
        // 占位
        out.writeBytes(LENGTH_PLACEHOLDER);

        Hessian2Output hessian2Output = null;
        try {
            hessian2Output = new Hessian2Output(new ByteBufOutputStream(out));
            hessian2Output.writeObject(msg);
            hessian2Output.flush();
        } finally {
            if (hessian2Output != null) {
                hessian2Output.close();
            }
        }

        int endIndex = out.writerIndex();

        out.setInt(startIndex, endIndex - startIndex - LENGTH_PLACEHOLDER.length);
    }
}
