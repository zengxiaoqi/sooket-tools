package com.tools.sockettools.tcp.start;

import com.tools.sockettools.tcp.server.ServerThread;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/*
 * 基于TCP协议的Socket通信，实现用户登陆
 * 服务器端
 */
@Data
@Slf4j
public class Server {
    private String id;
    private int port ;
    public static Map<String,Socket> connectMap = new HashMap<>();

    public ServerSocket createServer(Map config) throws IOException {
        port = Integer.parseInt((String)config.get("port"));
        id = (String)config.get("id");

        //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
        ServerSocket serverSocket=new ServerSocket(port);
        return serverSocket;
    }


    public void start(ServerSocket serverSocket) {
        try {
            Socket socket=null;
            //记录客户端的数量
            int count=0;
            System.out.println("***服务器即将启动，等待客户端的连接***");
            //循环监听等待客户端的连接
            while(true){
                //调用accept()方法开始监听，等待客户端的连接
                socket=serverSocket.accept();

                log.debug("accpt :"+ socket.toString());

                connectMap.put(id,socket);
                //创建一个新的线程

                ServerThread serverThread=new ServerThread(socket);
                //启动线程
                serverThread.start();

                count++;//统计客户端的数量
                System.out.println("客户端的数量："+count);
                InetAddress address=socket.getInetAddress();
                System.out.println("当前客户端的IP："+address.getHostAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
