package com.tools.sockettools.entity;

import lombok.Data;

@Data
public class HttpServerEntity {
    private String id;
    private String name;
    private Integer port;
    private String path;
    /**
     * 自动应答标志
     */
    private Boolean autoResp;
    private String respStr;
    private Boolean isHttps;
    private String keyPath;
    private String passWord;
    private String keyPassWord;
    private Integer timeout = 60000;
    private String encode;
}
