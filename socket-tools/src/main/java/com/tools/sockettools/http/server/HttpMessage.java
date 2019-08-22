package com.tools.sockettools.http.server;

import lombok.Data;

import java.util.Map;

@Data
public class HttpMessage {
    private String parentId;
    private String id;
    private Map recvHeaders;
    private Map recvParams;
    private String recvMsg;
    private Map respHeaders;
    private String respMsg;
    private boolean respFlag;

    public HttpMessage(String parentId,String id,Map<String,String> recvHeaders,Map<String,String> recvParams,String recvMsg){
        this.parentId = parentId;
        this.id = id;
        this.recvHeaders = recvHeaders;
        this.recvParams = recvParams;
        this.recvMsg = recvMsg;
        this.respFlag = false;
    }
}
