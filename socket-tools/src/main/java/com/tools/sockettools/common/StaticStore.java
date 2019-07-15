package com.tools.sockettools.common;

import com.tools.sockettools.util.JsonUtils;
import com.tools.sockettools.entity.ClientInfo;
import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.entity.ServerInfo;
import com.tools.sockettools.entity.WebsocketData;
import com.tools.sockettools.websocket.WebSocket;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class StaticStore {
    public static String WS_TYPE_SERVERLIST = "server-list";
    public static String WS_SHOP_ID = "TCP_SERVER";
    public static int MAX_ACCPT_POOL_SIZE = 20;
    /**
     * 根据ID保存socket套接字
     */
    public static Map<String,Socket> connectMap = new HashMap<>();
    public static Map<String,ExecutorService> accptPoolMap = new HashMap<>();
    public static Map<String,ExecutorService> serverPoolMap = new HashMap<>();
    public static Map<String,Boolean> serverMap = new HashMap<>();
    public static Map<Socket,StringBuffer> socketMap = new HashMap<>();
    /**
     * 保存服务监听端口和客户端连接信息
     */
    public static List<NodeTree> nodeTreeList = new ArrayList<NodeTree>();
    public static List<ClientInfo> clientList = new ArrayList<ClientInfo>();
    /**
     * 保存服务状态信息
     */
    public static Map<String, ServerInfo> serverList = new HashMap<String, ServerInfo>();
    //public static Map<String, ClientInfo> clientList = new HashMap<String, ClientInfo>();
    /**
     *
     * @param id
     * @param nodeTree
     */

    public static void addChildNodeTree(String id, NodeTree nodeTree){
        for(NodeTree pareNode : nodeTreeList) {
            if(pareNode.getId().equals(id)) {
                pareNode.addChildren(nodeTree);
                sendWebSocket(WS_TYPE_SERVERLIST,WS_SHOP_ID,nodeTreeList);
            }
        }
    }

    public static  void deleteChildById(String childId){
        /*iterator遍历过程加同步锁，锁住整个arrayList*/
        synchronized (nodeTreeList) {
            for (NodeTree pareNode : StaticStore.nodeTreeList) {
                if (null != pareNode.getChildren()) {
                    Iterator<NodeTree> iterator = pareNode.getChildren().iterator();
                    while (iterator.hasNext()) {
                        NodeTree childNode = iterator.next();
                        if (childNode.getId().equals(childId)) {
                            /*iterator.remove() 解决ConcurrentModificationException异常*/
                            iterator.remove();
                            sendWebSocket(WS_TYPE_SERVERLIST, WS_SHOP_ID, nodeTreeList);
                        }
                    }
                /*for (Object child : pareNode.getChildren()) {
                    NodeTree childNode = (NodeTree) child;
                    if (childNode.getId().equals(childId)) {
                        pareNode.removeChildren(childNode);
                        sendWebSocket(WS_TYPE_SERVERLIST, WS_SHOP_ID, nodeTreeList);
                    }
                }*/
                }
            }
        }

    }

    public static void deleteChildByParentId(String parentId){
        for(NodeTree pareNode : StaticStore.nodeTreeList) {
            if(pareNode.getId().equals(parentId)) {
                pareNode.setChildren(null);
                sendWebSocket(WS_TYPE_SERVERLIST, WS_SHOP_ID, nodeTreeList);
            }
        }
    }

    public static NodeTree getParentNodeById(String parentId){
        for(NodeTree pareNode : StaticStore.nodeTreeList) {
            if(pareNode.getId().equals(parentId)) {
                return pareNode;
            }
        }
        return null;
    }

    public static void sendWebSocket(String msgType, String shopId, Object obj){
        WebsocketData websocketData = new WebsocketData();
        websocketData.setType(msgType);
        websocketData.setMessage(obj);
        String rspMsg = null;
        try {
            rspMsg = JsonUtils.object2Json(websocketData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WebSocket.sendOneMessage(shopId, rspMsg);
    }

    public static ClientInfo getClientInfoById(String id){
        for(ClientInfo clientInfo : StaticStore.clientList){
            if(clientInfo.getId().equals(id)){
                return clientInfo;
            }
        }
        return null;
    }

    public static boolean delClientInfoById(String id){
        for(ClientInfo clientInfo : StaticStore.clientList){
            if(clientInfo.getId().equals(id)){
                return StaticStore.clientList.remove(clientInfo);
            }
        }
        return false;
    }

    public static ClientInfo setClientInfoById(String id, ClientInfo newClientInfo){
        int i=0;
        for(ClientInfo clientInfo : StaticStore.clientList){
            if(clientInfo.getId().equals(id)){
                return StaticStore.clientList.set(i, newClientInfo);
            }
            i++;
        }
        return null;
    }

    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
        System.out.println("-----------线程池清理结束--------");
    }
}
