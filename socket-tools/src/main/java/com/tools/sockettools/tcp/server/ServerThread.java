package com.tools.sockettools.tcp.server;

import com.tools.sockettools.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/*
 * 服务器线程处理类
 */
@Slf4j
public class ServerThread extends Thread {
    public static Map<Socket,StringBuffer> socketMap = new HashMap<>();
    // 和本线程相关的Socket
    Socket socket = null;

    public ServerThread(Socket socket) {
        this.socket = socket;
        socketMap.put(socket,new StringBuffer());
    }

    //线程执行的操作，响应客户端的请求
    @Override
    public void run(){
        System.out.println("--------------------");
        InputStream is=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        OutputStream os=null;
        PrintWriter pw=null;
        try {
            //获取输入流，并读取客户端信息
            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            socketMap.get(socket).append("["+DateUtil.getNowStrDate()+"]"+"收到数据: ");
            String info=null;

            while((info=br.readLine())!=null){
                //循环读取客户端的信息
                //System.out.println("我是服务器，客户端说："+info);
                System.out.println(socketMap.get(socket));
                socketMap.get(socket).append(info);
            }
            socket.shutdownInput();//关闭输入流

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //关闭资源
            try {
                if(pw!=null)
                    pw.close();
                if(os!=null)
                    os.close();
                if(br!=null)
                    br.close();
                if(isr!=null)
                    isr.close();
                if(is!=null)
                    is.close();
                /*if(socket!=null)
                    socket.close();*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
