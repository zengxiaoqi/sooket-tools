package com.tools.sockettools.http.server;

import com.tools.sockettools.common.StaticStore;
import com.tools.sockettools.entity.HttpServerEntity;
import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.http.common.ProxyRequestHelper;
import com.tools.sockettools.util.ByteUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.map.HashedMap;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Data
@Slf4j
public class HttpListener extends HttpServlet {
    private AtomicInteger count = new AtomicInteger(0);
    private long closedTimeOut = 60000;
    private Server httpServer;
    private HttpServerEntity httpServerEntity;
    @SuppressWarnings("unused")
    private JettyThreadPool jettyThreadPool;
    private List<HttpServerInfo> httpServerInfoList;

    public HttpListener(HttpServerEntity httpServerEntity) {
        this.httpServerEntity = httpServerEntity;
        this.httpServerInfoList = new ArrayList<>();
        httpServer = new Server();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        httpServer.setHandler(contexts);
        jettyThreadPool = new JettyThreadPool();
        httpServer.setThreadPool(jettyThreadPool);
        Connector connector = null;
        if (!httpServerEntity.getIsHttps()) {
            connector = new SelectChannelConnector();
            ((SelectChannelConnector) connector).setAcceptors(1);
            connector.setHost("0.0.0.0");
            connector.setPort(httpServerEntity.getPort());
            connector.setMaxIdleTime((int) httpServerEntity.getTimeout());
            ((SelectChannelConnector) connector).setAcceptQueueSize(Integer.MAX_VALUE);
            ((SelectChannelConnector) connector).setConfidentialScheme("http");
        } else {
            connector = new SslSocketConnector();
            ((SslSocketConnector) connector).setAcceptors(1);
            ((SslSocketConnector) connector).setHost("0.0.0.0");
            ((SslSocketConnector) connector).setPort(httpServerEntity.getPort());
            ((SslSocketConnector) connector).setMaxIdleTime(httpServerEntity.getTimeout());
            ((SslSocketConnector) connector).setAcceptQueueSize(Integer.MAX_VALUE);
            ((SslSocketConnector) connector).setKeystore(Resource.newClassPathResource(httpServerEntity.getKeyPath()).getName());
            ((SslSocketConnector) connector).setKeyPassword(httpServerEntity.getPassWord());
            ((SslSocketConnector) connector).setPassword(httpServerEntity.getKeyPassWord());
            ((SslSocketConnector) connector).setProtocol("SSL");
            ((SslSocketConnector) connector).setConfidentialScheme("https");

        }

        httpServer.setConnectors(new Connector[] { connector });

        ServletHandler servletHandler = new ServletHandler();
        ServletHolder servletHolder = new ServletHolder();
        servletHolder.setServlet(this);
        servletHandler.addServletWithMapping(servletHolder, httpServerEntity.getPath());
        httpServer.setHandler(servletHandler);
        try {
            httpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("HttpListener error [{}]", e.getMessage());
        }

    }

    /**
     * convert get message to post
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("HttpListener client: [{}] do get request", req.getRemoteAddr());
        doPost(req,resp);
    }

    /**
     * do post message
     *
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        count.incrementAndGet();

        Thread.currentThread().setName("HTTP#dopost-" + count.get());
        log.debug("ThreadName:{},Count:{},HttpListener client: [{}] begin dopost", Thread.currentThread().getName(), count.get(), req.getRemoteAddr());
        /* 增加子节点 */
        NodeTree nodeTree = new NodeTree();
        nodeTree.setParentId(httpServerEntity.getId());
        nodeTree.setName(req.getRemoteAddr()+"-"+String.valueOf(count.get()));
        nodeTree.setId(String.valueOf(count.get()));
        nodeTree.setLeaf(true);
        StaticStore.addChildNodeTree(StaticStore.httpServerTree,httpServerEntity.getId(), nodeTree,StaticStore.WS_TYPE_HttpSERVERLIST);

        boolean closedFlag = false;
        req.setAttribute("CLOSEDFLAG", "FALSE");
        req.setAttribute("REQCOUNT", String.valueOf(count.get()));

        InputStream in = req.getInputStream();
        int l = req.getContentLength();
        byte[] reqMsg = new byte[l];
        int readCount = 0;
        while (readCount < l) {
            readCount += in.read(reqMsg, readCount, l - readCount);
        }
        in.close();

        try {
            HttpServerInfo httpServerInfo = new HttpServerInfo();
            httpServerInfo.setParentId(httpServerEntity.getId());
            httpServerInfo.setReq(req);
            httpServerInfo.setResp(resp);
            httpServerInfo.setId(String.valueOf(count.get()));
            //httpServerInfo.setRecvMsg(new String(reqMsg,this.httpServerEntity.getEncode()));
            httpServerInfoList.add(httpServerInfo);
            log.info("接收报文：[{}]",new String(reqMsg,this.httpServerEntity.getEncode()));
            ProxyRequestHelper requestHelper = new ProxyRequestHelper();
            HttpMessage httpMessage = new HttpMessage(httpServerEntity.getId(),
                    String.valueOf(count.get()),
                    getRequestHeaders(req),getRequestParams(req),
                    new String(reqMsg,this.httpServerEntity.getEncode()));
            StaticStore.httpMessageList.add(httpMessage);

            long currTime = System.currentTimeMillis();
            while (true) {
                //超时时间
                if (/*System.currentTimeMillis() - currTime > closedTimeOut || */closedFlag) {
                    log.debug("doHttpPost,return. {}ms", System.currentTimeMillis() - currTime);
                    break;
                }
                if(httpServerEntity.getAutoResp()){
                    //发送应答
                    send(httpServerInfo, httpServerEntity.getRespStr().getBytes());
                }
                String s = (String) req.getAttribute("CLOSEDFLAG");
                if (s != null && s.equals("TRUE")) {
                    closedFlag = true;
                }
//				Thread.sleep(10);
//				logger.info("doPost end,cast#{}, ThreadName:{},Count:{},HttpListener client: [{}] begin dopost", System.currentTimeMillis()
//						- currTime, Thread.currentThread().getName(), count.get(), req.getRemoteAddr());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("");
        }
    }

    /**
     * send message to remote client
     *
     * @throws Exception
     */
    public void send(HttpServerInfo conn, byte[] message) throws Exception {
        try {
            log.debug("Http Response to client," + ByteUtil.dumphex((byte[]) message));
            javax.servlet.http.HttpServletRequest req = conn.getReq();
            javax.servlet.http.HttpServletResponse resp = conn.getResp();
            OutputStream out = resp.getOutputStream();
            resp.setContentLength(message.length);
            out.write(message);
            out.close();
            req.setAttribute("CLOSEDFLAG", "TRUE");

            //移除连接
            httpServerInfoList.remove(conn);
        } catch (Exception e) {
            log.error("Http发送异常," + e.getMessage());
            throw e;
        }
    }

    /**
     * close httpserver
     *
     * @throws Exception
     */
    public void close() throws Exception {
        try {
            httpServer.stop();
            httpServer.destroy();
        } catch (Exception e) {
            log.error("Http关闭异常," + e.getMessage());
            throw e;
        }
    }

    public Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashedMap();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    String value = values.nextElement();
                    headers.put(name, value);
                }

            }
        }
        return headers;
    }

    public Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> params = new HashedMap();
        Enumeration<String> paramNames = request.getParameterNames();
        if (paramNames != null) {
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    String value = values.nextElement();
                    params.put(name, value);
                }

            }
        }
        return params;
    }
}
