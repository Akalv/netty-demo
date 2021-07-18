package http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.net.URL;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri = msg.uri();
        // 默认访问index.html
        if (uri.equals("/")) {
            uri = "/index.html";
        }

        // 构造响应
        FullHttpResponse response;
        // 获取静态资源
        URL resource = Thread.currentThread().getContextClassLoader().getResource(uri.substring(1));
        try {
            if (resource != null) {
//                response =
            } else {
                uri = "/404.html";
            }
        }
    }

    private FullHttpResponse buildResponse(String uri, HttpResponseStatus status) {
        return null;
    }

    private void buildContentType(FullHttpResponse response, String uri) {
        return;
    }
}
