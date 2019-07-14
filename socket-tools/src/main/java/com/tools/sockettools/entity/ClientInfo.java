package com.tools.sockettools.entity;

import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.ServerSocket;
import java.net.Socket;

@Data
public class ClientInfo {
    //定义jackson对象，用于转成json字符串
    private static final ObjectMapper MAPPER = new ObjectMapper();

    String id;  //id
    String ip; //本地IP
    int port; //端口端口
    String remoteIp; //远程ip
    int remotePort; //远程端口
    boolean status;  //监听状态
    String encode;  //编码
    String recvMsg; //接收到的消息
    String sendMsg;
    boolean hexStr; //发送是否为hex码
}
