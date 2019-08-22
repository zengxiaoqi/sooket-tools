package com.tools.sockettools.entity;

import lombok.Data;

import java.util.Map;

@Data
public class HttpSendRespEntity {
    private String parentId;
    private String id;
    private String checkedHex;
    private Map sendHeaders;
    private String sendMsg;
}
