package com.tools.sockettools.entity;

import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.ServerSocket;
import java.net.Socket;

@Data
public class ServerInfo {
    //定义jackson对象，用于转成json字符串
    private static final ObjectMapper MAPPER = new ObjectMapper();

    String id;  //id
    String port; //端口
    String status;  //监听状态
    String encode;  //编码
    ServerSocket serverSocket;  //监听socket
    Socket socket;  //连接socket
    String message; //接收到的消息
    //List<NodeTree> nodeTreeList;    //连接点
}
