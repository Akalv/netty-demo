package bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

public class TimeServer {

    public void bind(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("服务器启动成功");

            while (true) {
                System.out.println("等待客户端链接");

                // 阻塞
                Socket socket = serverSocket.accept();

                System.out.println("客户端链接成功");

                // 开启新线程处理
                // TODO 优化成线程池
                new Thread(new TimeServerHandler(socket)).start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class TimeServerHandler implements Runnable {

        private Socket socket;

        public TimeServerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(
                         new OutputStreamWriter(socket.getOutputStream())
                 )
            ){
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("接收到客户端数据：" + line);
                    if (line.equalsIgnoreCase("Get Date")) {
                        writer.write(new Date().toString());
                    } else {
                        writer.write("WRONG REQUEST");
                    }

                    writer.newLine();

                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new TimeServer().bind(8888);
    }
}
