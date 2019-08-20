package com.tools.sockettools.http.server;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Data
public class HttpServerInfo {
    private String parentId;
    private String id;
    private HttpServletRequest req;
    private HttpServletResponse resp;
}
