package com.tools.sockettools.tcp.server;

import com.tools.sockettools.common.util.DateUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SendThread implements Runnable{
    private Socket socket;
    private String sendMsg;
    private String encode;

    public SendThread(Socket socket, String sendMsg,String encode) {
        this.socket=socket;
        this.sendMsg = sendMsg;
        this.encode = encode;
        // TODO Auto-generated constructor stub
    }
    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(sendMsg.getBytes(encode));
            StaticStore.socketMap.get(socket).append("["+DateUtil.getNowStrDate()+"]"+"发送数据: ");
            StaticStore.socketMap.get(socket).append(sendMsg).append("\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
