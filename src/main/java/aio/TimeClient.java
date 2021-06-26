package aio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class TimeClient {
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void connect(String ip, int port) {
        try (
                AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open()
                ){
            socketChannel.connect(new InetSocketAddress(InetAddress.getByName(ip), port), null, new CompletionHandler<Void, Object>() {
                @Override
                public void completed(Void result, Object attachment) {
                    System.out.println("与服务端链接成功.");

                    doRead(socketChannel);

                    doWrite(socketChannel);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    try {
                        socketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doRead(AsynchronousSocketChannel socketChannel) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        socketChannel.read(byteBuffer, socketChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel attachment) {
                try {
                    doRead(socketChannel);
                    if (result == -1) {
                        attachment.close();
                    } else {
                        byteBuffer.flip();
                        byte[] bts = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bts);
                        String str = new String(bts);
                        System.out.println("接收到服务端数据：" + str);
                        byteBuffer.clear();

                        doWrite(socketChannel);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
                try {
                    attachment.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doWrite(AsynchronousSocketChannel socketChannel) {
        System.out.println("请输入要发送的数据：");
        String input = new Scanner(System.in).nextLine();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(input.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }

    public static void main(String[] args) {
        new TimeClient().connect("127.0.0.1", 8888);
    }
}
