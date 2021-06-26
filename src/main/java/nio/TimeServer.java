package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class TimeServer {

    public void bind(int port) {
        try (ServerSocketChannel channel = ServerSocketChannel.open()){
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);

            while (selector.select() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    if (next.isAcceptable()) {
                        // 如果是通知ACCEPET就绪
                        System.out.println("客户端链接成功");
                        ServerSocketChannel tmpChannel = (ServerSocketChannel) next.channel();
                        SocketChannel channel1 = tmpChannel.accept();
                        channel1.configureBlocking(false);
                        channel1.register(selector, SelectionKey.OP_READ);
                    } else if (next.isReadable()){
                        SocketChannel socketChannel = (SocketChannel) next.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int size = socketChannel.read(buffer);
                        if (size == -1) {
                            // 客户端关闭了链接
                            socketChannel.close();
                            return;
                        }

                        buffer.flip();
                        byte[] bts = new byte[buffer.remaining()];
                        buffer.get(bts);
                        String str = new String(bts);
                        buffer.clear();
                        System.out.println("接收到客户端数据：" + str);

                        if (str.equalsIgnoreCase("Get Date")) {
                            buffer.put(new Date().toString().getBytes());
                        } else {
                            buffer.put("WRONG REQUEST".getBytes());
                        }
                        buffer.flip();
                        socketChannel.write(buffer);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TimeServer().bind(8888);
    }
}
