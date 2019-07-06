package com.tools.sockettools.tcp.server;

import com.tools.sockettools.common.util.DateUtil;
import com.tools.sockettools.common.util.JsonUtils;
import com.tools.sockettools.control.TcpServerControl;
import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.entity.WebsocketData;
import com.tools.sockettools.websocket.WebSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/*
 * 服务器线程处理类
 */
@Slf4j
public class RecvThread extends Thread {
    public static Map<Socket,StringBuffer> socketMap = new HashMap<>();
    // 和本线程相关的Socket
    Socket socket = null;

    public RecvThread(Socket socket) {
        this.socket = socket;
        socketMap.put(socket,new StringBuffer());
    }

    //线程执行的操作，响应客户端的请求
    @Override
    public void run(){
        System.out.println("--------------------");
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        try {
            // 装饰流BufferedReader封装输入流（接收客户端的流）
            bis = new BufferedInputStream(socket.getInputStream());
            dis = new DataInputStream(bis);
            byte[] bytes = new byte[1]; // 一次读取一个byte
            byte[] MAX_BYTES = new byte[1024*1024];
            int i=0;
            while (dis.read(bytes) != -1) {
                MAX_BYTES[i++] = bytes[0];
                if (dis.available() == 0) {
                    //接收完一个报文
                    appendMsg(socket, byteToStr(MAX_BYTES));
                    i=0;
                    java.util.Arrays.fill(MAX_BYTES, (byte)0);
                }
            }

            System.out.println("客户端断开连接");
            //删除TcpServerControl.nodeTreeList中对应节点
            String childId = socket.getLocalSocketAddress() + ":" + socket.getPort();
            for(NodeTree pareNode : TcpServerControl.nodeTreeList) {
                for(Object child : pareNode.getChildren()) {
                    NodeTree childNode = (NodeTree)child;
                    if(childNode.getId().equals(childId)){
                        pareNode.removeChildren(childNode);
                        WebsocketData websocketData = new WebsocketData();
                        websocketData.setType("server-list");
                        websocketData.setMessage(TcpServerControl.nodeTreeList);
                        String rspMsg = null;
                        try {
                            rspMsg = JsonUtils.object2Json(websocketData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        WebSocket.sendOneMessage("TCP_SERVER", rspMsg);
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            System.out.println("-------关闭资源---------");
            //关闭资源
            try {
                if(dis!=null) {
                    dis.close();
                }
                if(bis!=null) {
                    bis.close();
                }
                if (socket != null){
                    socket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String byteToStr(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static void appendMsg(Socket socket, String msg){
        socketMap.get(socket).append("["+DateUtil.getNowStrDate()+"]"+"收到数据: ");
        socketMap.get(socket).append(msg).append("\n");
        System.out.println(socketMap.get(socket));
    }
}
