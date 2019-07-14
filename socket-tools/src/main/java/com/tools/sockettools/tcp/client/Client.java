package com.tools.sockettools.tcp.client;

import com.tools.sockettools.tcp.server.RecvThread;
import com.tools.sockettools.tcp.server.SendThread;
import com.tools.sockettools.tcp.server.StaticStore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 客户端
 */
@Slf4j
@Data
public class Client {
    private String id;
    private int port ;
    private String ip;
    private String encode;

    private ExecutorService pool;

    public Socket createClient(Map config) {
        if(config.get("port") instanceof String) {
            port = Integer.parseInt((String) config.get("port"));
        }else if(config.get("port") instanceof Integer){
            port = (Integer) config.get("port");
        }
        ip = (String)config.get("ip");
        encode = (String)config.get("encode");
        Socket socket= null;
        try {
            socket = new Socket(ip, port);
            id = socket.getInetAddress().getHostAddress()+":"+socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StaticStore.connectMap.put(id,socket);

        pool = Executors.newFixedThreadPool(2);
        StaticStore.accptPoolMap.put(id, pool);

        return socket;
    }

    public void closeClient(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recvMsg(Socket socket, String encode) {
        RecvThread recvThread = new RecvThread(socket,encode);
        pool.execute(recvThread);
        return ;
    }

    public void sendMsg(Socket socket, String sendMsg,String encode,boolean hexStr) {
        SendThread sendThread = new SendThread(socket,sendMsg,encode,hexStr);
        pool.execute(sendThread);
        return ;
    }
}
