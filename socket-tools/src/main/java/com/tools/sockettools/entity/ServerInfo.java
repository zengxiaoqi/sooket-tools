package com.tools.sockettools.entity;

import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.ServerSocket;
import java.util.List;

@Data
public class ServerInfo {
    //定义jackson对象，用于转成json字符串
    private static final ObjectMapper MAPPER = new ObjectMapper();

    String id;  //id
    String status;  //监听状态
    ServerSocket serverSocket;  //监听socket
    List<NodeTree> nodeTreeList;    //连接点
}
