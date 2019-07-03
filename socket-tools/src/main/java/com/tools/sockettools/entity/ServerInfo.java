package com.tools.sockettools.entity;

import lombok.Data;

import java.net.ServerSocket;
import java.util.List;

@Data
public class ServerInfo {
    String id;  //id
    String status;  //监听状态
    ServerSocket serverSocket;  //监听socket
    List<NodeTree> nodeTreeList;    //连接点
}
