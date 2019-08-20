package com.tools.sockettools.entity;

import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.Serializable;

@Data
public class NodeTree extends BaseTree implements Serializable {
    //定义jackson对象，用于转成json字符串
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String id; //id
    private String name;
}
