package com.tools.sockettools.tcp.server;

import com.tools.sockettools.common.util.JsonUtils;
import com.tools.sockettools.control.TcpServerControl;
import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.entity.ServerInfo;
import com.tools.sockettools.entity.WebsocketData;
import com.tools.sockettools.tcp.start.Server;
import com.tools.sockettools.websocket.WebSocket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

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

    /**
     * 保存服务状态信息
     */
    public static Map<String, ServerInfo> serverList = new HashMap<String, ServerInfo>();
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

    public static void deleteChildById(String childId){
        for(NodeTree pareNode : StaticStore.nodeTreeList) {
            if(null != pareNode.getChildren()) {
                for (Object child : pareNode.getChildren()) {
                    NodeTree childNode = (NodeTree) child;
                    if (childNode.getId().equals(childId)) {
                        pareNode.removeChildren(childNode);
                        sendWebSocket(WS_TYPE_SERVERLIST, WS_SHOP_ID, nodeTreeList);
                    }
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
}
