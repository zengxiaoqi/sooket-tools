package com.tools.sockettools.control;

import com.tools.sockettools.common.util.StringUtil;
import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.entity.ServerInfo;
import com.tools.sockettools.tcp.server.RecvThread;
import com.tools.sockettools.tcp.server.SendThread;
import com.tools.sockettools.tcp.server.StaticStore;
import com.tools.sockettools.tcp.start.Server;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zengxq
 */
@RestController
public class TcpServerControl {

    @RequestMapping(value="/getIP",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getIP(HttpServletRequest httpRequest) {
        return ReturnResult.success((Object)httpRequest.getRemoteAddr());
    }

    @RequestMapping(value="/createServer",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult createServer(@RequestBody Map<String,Object> config) {
        ReturnResult returnResult = new ReturnResult();

        String id = String.format("%s:%s",config.get("ip"), config.get("port"));
        config.put("id", id);
        try {
            Server server = new Server(config);
            ExecutorService pool = Executors.newFixedThreadPool(1);
            pool.execute(server);
            StaticStore.serverPoolMap.put(id, pool);
            returnResult.setSuccess(true);

            ServerInfo serverInfo = new ServerInfo();
            //保存监听返回的 ServerSocket ，供后续关闭使用
            serverInfo.setId(id);
            serverInfo.setPort((String)config.get("port"));
            serverInfo.setServerSocket(server.getServerSocket());
            serverInfo.setStatus("open");
            serverInfo.setEncode((String)config.get("encode"));

            NodeTree nodeTree = new NodeTree();
            nodeTree.setId(id);
            nodeTree.setLeaf(false);

            //新建服务
            StaticStore.serverList.put(id, serverInfo);
            StaticStore.nodeTreeList.add(nodeTree);

            returnResult.setData(StaticStore.nodeTreeList);
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("新建服务失败："+e.getMessage());
        }

        return returnResult;
    }

    @RequestMapping(value="/startServer",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult startServer(@RequestBody Map<String,Object> config) {
        ReturnResult returnResult = new ReturnResult();
        String id = (String)config.get("id");
        ServerInfo serverInfo = StaticStore.serverList.get(id);
        config.put("port", serverInfo.getPort());
        Server server = null;
        try {
            server = new Server(config);
            new Thread(server).start();
            //StaticStore.serverPoolMap.get(id).execute(server);
            //重启监听
            serverInfo.setServerSocket(server.getServerSocket());
            StaticStore.serverList.get(id).setStatus("open");
            returnResult.setSuccess(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        returnResult.setData(StaticStore.nodeTreeList);

        return returnResult;
    }

    @RequestMapping(value="/stopServer",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult stopServer(@RequestParam("id") String id) {
        ReturnResult returnResult = new ReturnResult();

        Server server = new Server();
        server.stop(id,StaticStore.serverList.get(id).getServerSocket());
        StaticStore.shutdownAndAwaitTermination(StaticStore.serverPoolMap.get(id));

        returnResult.setSuccess(true);

        StaticStore.serverList.get(id).setStatus("close");
        /* 清空nodeList内容？ */
        //StaticStore.deleteChildByParentId(id);
        //returnResult.setData(StaticStore.nodeTreeList);

        return returnResult;
    }
    @RequestMapping(value="/delServer",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult delServer(@RequestParam("id") String id) {
        ReturnResult returnResult = new ReturnResult();

        stopServer(id);

        returnResult.setSuccess(true);

        StaticStore.serverList.remove(id);
        StaticStore.serverMap.remove(id);

        NodeTree pareNode = StaticStore.getParentNodeById(id);
        StaticStore.nodeTreeList.remove(pareNode);

        returnResult.setData(StaticStore.nodeTreeList);

        return returnResult;
    }

    @RequestMapping(value="/sendRespons",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult sendRespons(@RequestBody Map<String,Object> config) {
        ReturnResult returnResult = new ReturnResult();
        try {
            Socket socket = StaticStore.connectMap.get(config.get("id"));
            String rspMsg = (String) config.get("sendMsg");

            ServerInfo serverInfo = StaticStore.serverList.get(config.get("parentId"));
            SendThread sendThread = new SendThread(socket,rspMsg,serverInfo.getEncode(),(Boolean) config.get("hexStr"));
            new Thread(sendThread).start();
            returnResult.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnResult;
    }

    @RequestMapping(value="/getServerList",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getServerList(@RequestParam("id") String id) {
        ReturnResult returnResult = new ReturnResult();

        returnResult.setSuccess(true);
        ServerInfo serverInfo = StaticStore.serverList.get(id);
        Map<String, Boolean> rspMap = new HashedMap();
        rspMap.put("status", serverSocketStatus(serverInfo.getServerSocket()));
        returnResult.setData(rspMap);
        return returnResult;
    }

    @RequestMapping(value="/getRcvMsg",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getRcvMsg(@RequestParam("id") String id) {
        ReturnResult returnResult = new ReturnResult();
        Socket socket = null;
        socket = StaticStore.connectMap.get(id);
        StringBuffer stringBuffer = StaticStore.socketMap.get(socket);

        returnResult.setSuccess(true);
        returnResult.setData(stringBuffer.toString());
        return returnResult;
    }

    @RequestMapping(value="/getNodeTree",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getNodeTree() {
        ReturnResult returnResult = new ReturnResult();
        returnResult.setSuccess(true);
        returnResult.setData(StaticStore.nodeTreeList);
        return returnResult;
    }

    @RequestMapping(value="/getSocketInfo",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getSocketInfo(@RequestParam("parentId") String parentId,@RequestParam("id") String id) {
        ReturnResult returnResult = new ReturnResult();
        Map<String, Object> rspMap = new HashMap<>();
        ServerInfo serverInfo = StaticStore.serverList.get(parentId);

        Socket socket = null;
        socket = StaticStore.connectMap.get(id);
        StringBuffer stringBuffer = StaticStore.socketMap.get(socket);

        rspMap.put("ip", socket.getLocalAddress().toString());
        rspMap.put("port", socket.getLocalPort());
        rspMap.put("remoteIp",socket.getInetAddress().getHostAddress());
        rspMap.put("remotePort",socket.getPort());
        rspMap.put("message",stringBuffer.toString());
        rspMap.put("status",serverInfo.getStatus());

        List list = new ArrayList();
        list.add(rspMap);

        returnResult.setSuccess(true);
        returnResult.setData(list);
        return returnResult;
    }

    public Boolean serverSocketStatus(ServerSocket serverSocket){
        return !serverSocket.isClosed();
    }
}
