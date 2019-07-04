package com.tools.sockettools.entity;

import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;

@Data
public class WebsocketData {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    /**
     * type： 发送的数据类型，前端根据类型解析message
     */
    String type;
    Object message;
}
