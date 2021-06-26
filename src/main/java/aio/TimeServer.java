package aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class TimeServer {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void bind(int port) {
        try (
                AsynchronousServerSocketChannel serverSocketChannel =
                        AsynchronousServerSocketChannel.open();
                ){
            serverSocketChannel.bind(new InetSocketAddress(port));

            doAccept(serverSocketChannel);

            // 因为非上面是阻塞的，所以这里需要阻塞住主线程

            countDownLatch.await();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept(AsynchronousServerSocketChannel serverSocketChannel) {
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                //因为doAccept不会循环调用，为了能够处理更多客户端，而不是一个，需要再触发一次
                doAccept(serverSocketChannel);

                doRead(result);

                System.out.println("客户端链接");
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
                countDownLatch.countDown();
            }
        });
    }

    private void doRead(AsynchronousSocketChannel socketChannel) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        socketChannel.read(byteBuffer, socketChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel attachment) {
                doRead(attachment);
                try {
                    if (result == -1) {
                        attachment.close();
                    } else {
                        byteBuffer.flip();
                        byte[] bts = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bts);
                        String str = new String(bts);
                        System.out.println("接收到客户端数据：" + str);
                        byteBuffer.clear();
                        if (str.equalsIgnoreCase("Get Date")) {
                            byteBuffer.put(new Date().toString().getBytes());
                        } else {
                            byteBuffer.put("WRONG REQUEST".getBytes());
                        }
                        byteBuffer.flip();
                        attachment.write(byteBuffer);
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

    public static void main(String[] args) {
        new TimeServer().bind(8888);
    }
}
