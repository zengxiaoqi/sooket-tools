package com.tools.sockettools.tcp.server;

import com.tools.sockettools.common.util.DateUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SendThread implements Runnable{
    private Socket socket;
    private String sendMsg;
    public SendThread(Socket socket, String sendMsg) {
        this.socket=socket;
        this.sendMsg = sendMsg;
        // TODO Auto-generated constructor stub
    }
    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(sendMsg.getBytes());
            RecvThread.socketMap.get(socket).append("["+DateUtil.getNowStrDate()+"]"+"发送数据: ");
            RecvThread.socketMap.get(socket).append(sendMsg).append("\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
