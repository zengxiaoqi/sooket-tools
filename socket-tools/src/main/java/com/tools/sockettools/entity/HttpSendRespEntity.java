package com.tools.sockettools.entity;

import lombok.Data;

@Data
public class HttpSendRespEntity {
    private String parentId;
    private String id;
    private String hexStr;
    private String respStr;
}
