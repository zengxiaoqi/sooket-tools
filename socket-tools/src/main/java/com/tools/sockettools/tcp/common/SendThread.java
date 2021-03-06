package com.tools.sockettools.tcp.common;

import com.tools.sockettools.common.StaticStore;
import com.tools.sockettools.util.DateUtil;
import com.tools.sockettools.util.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SendThread implements Runnable{
    private Socket socket;
    private String sendMsg;
    private String encode;
    private boolean hexStr;

    public SendThread(Socket socket, String sendMsg,String encode,boolean hexStr) {
        this.socket=socket;
        this.sendMsg = sendMsg;
        this.encode = encode;
        this.hexStr = hexStr;
        // TODO Auto-generated constructor stub
    }
    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            if (hexStr){
                outputStream.write(StringUtil.hexStr2Bytes(sendMsg));
            }else {
                outputStream.write(sendMsg.getBytes(encode));
            }
            outputStream.flush();
            StaticStore.socketMap.get(socket).append("["+DateUtil.getNowStrDate()+"]"+"发送数据: ");
            StaticStore.socketMap.get(socket).append(sendMsg).append("\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
