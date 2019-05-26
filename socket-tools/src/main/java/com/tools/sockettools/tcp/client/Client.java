package com.tools.sockettools.tcp.client;

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

/*
 * 客户端
 */
public class Client {
    private String id;
    private int port ;
    private String ip;
    public static Map<String,Socket> connectMap = new HashMap<>();

    public Socket createClient(Map config) {
        port = Integer.parseInt((String)config.get("port"));
        ip = (String)config.get("ip");
        id = (String)config.get("id");
        Socket socket= null;
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectMap.put(id,socket);
        return socket;
    }

    public void closeClient(Map config) {
        id = (String)config.get("id");
        Socket socket= connectMap.get(id);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMsg(Socket socket, String Msg) {
        String info=null;
        try {
            //1.创建客户端Socket，指定服务器地址和端口

            //2.获取输出流，向服务器端发送信息
            OutputStream os=socket.getOutputStream();//字节输出流
            /*os.write(Msg);*/
            PrintWriter pw=new PrintWriter(os);//将输出流包装为打印流
            pw.write(Msg);
            pw.flush();
            socket.shutdownOutput();//关闭输出流
            //3.获取输入流，并读取服务器端的响应信息
            InputStream is=socket.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(is));

            while((info=br.readLine())!=null){
                System.out.println("我是客户端，服务器说："+info);
            }
            //4.关闭资源
            br.close();
            is.close();
            pw.close();
            os.close();
            //socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return info;
    }
}
