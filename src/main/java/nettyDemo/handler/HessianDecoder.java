package nettyDemo.handler;

import com.caucho.hessian.io.Hessian2Input;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class HessianDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            in.resetReaderIndex();
            return;
        }
        int contentLen = in.readInt();
        if (in.readableBytes() < contentLen) {
            in.readerIndex(in.readerIndex() - 4);
            return;
        }

        Hessian2Input hessian2Input = null;
        try {
            hessian2Input = new Hessian2Input(new ByteBufInputStream(in));
            Object object = hessian2Input.readObject();
            out.add(object);
        } finally {
            if (hessian2Input != null) {
                hessian2Input.close();
            }
        }
    }
}
