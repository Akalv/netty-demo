package nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class TimeClient {

    public void bind(String ip, int port) {
        try (
                SocketChannel connectChannel = SocketChannel.open();
                ){
            connectChannel.configureBlocking(false);
            connectChannel.connect(new InetSocketAddress(InetAddress.getByName(ip), port));

            Selector selector = Selector.open();
            connectChannel.register(selector, SelectionKey.OP_CONNECT);

            while (selector.select() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    if (next.isConnectable()) {
                        SocketChannel socketChannel = (SocketChannel) next.channel();
                        socketChannel.configureBlocking(false);
                        if (socketChannel.finishConnect()) {
                            writeMsg(socketChannel);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                    } else if (next.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) next.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int size = socketChannel.read(byteBuffer);
                        if (size == -1) {
                            socketChannel.close();
                            return;
                        }
                        byteBuffer.flip();
                        byte[] bts = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bts);
                        String str = new String(bts);
                        System.out.println("接收到来自服务端的消息：" + str);
                        writeMsg(socketChannel);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMsg(SocketChannel channel) throws IOException {
        System.out.println("请输入要发送的数据：");
        String input = new Scanner(System.in).nextLine();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(input.getBytes());
        buffer.flip();
        channel.write(buffer);
    }

    public static void main(String[] args) {
        new TimeClient().bind("127.0.0.1", 8888);
    }
}
