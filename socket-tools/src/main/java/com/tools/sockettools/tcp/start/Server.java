package com.tools.sockettools.tcp.start;

import com.tools.sockettools.common.util.JsonUtils;
import com.tools.sockettools.control.TcpServerControl;
import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.entity.WebsocketData;
import com.tools.sockettools.tcp.server.RecvThread;
import com.tools.sockettools.websocket.WebSocket;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/*
 * 基于TCP协议的Socket通信，实现用户登陆
 * 服务器端
 */
@Data
@Slf4j
public class Server {
    private String id;
    private int port ;
    public static Map<String,Socket> connectMap = new HashMap<>();

    private WebsocketData websocketData;

    public ServerSocket createServer(Map config) throws IOException {
        port = Integer.parseInt((String)config.get("port"));
        id = (String)config.get("id");

        //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
        ServerSocket serverSocket=new ServerSocket(port);
        websocketData = new WebsocketData();
        return serverSocket;
    }


    public void start(ServerSocket serverSocket) {
        try {
            Socket socket=null;
            //记录客户端的数量
            int count=0;
            System.out.println("***服务器即将启动，等待客户端的连接***");
            //循环监听等待客户端的连接
            while(true){
                //调用accept()方法开始监听，等待客户端的连接
                socket=serverSocket.accept();

                log.debug("accpt :"+ socket.toString());

                String childId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                connectMap.put(childId,socket);

                NodeTree nodeTree = new NodeTree();

                nodeTree.setParentId(id);
                nodeTree.setId(childId);
                nodeTree.setLeaf(true);
                for(NodeTree pareNode : TcpServerControl.nodeTreeList) {
                    if(pareNode.getId().equals(id)) {
                        pareNode.addChildren(nodeTree);
                        try {
                            //String rspMsg = JsonUtils.object2Json(TcpServerControl.nodeTreeList);
                            websocketData.setType("server-list");
                            websocketData.setMessage(TcpServerControl.nodeTreeList);
                            String rspMsg = JsonUtils.object2Json(websocketData);
                            WebSocket.sendOneMessage("TCP_SERVER", rspMsg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                //创建一个新的线程
                RecvThread recvThread =new RecvThread(socket);
                //启动线程
                recvThread.start();

                count++;//统计客户端的数量
                System.out.println("客户端的数量："+count);
                InetAddress address=socket.getInetAddress();
                System.out.println("当前客户端的IP："+address.getHostAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean stopServer(ServerSocket serverSocket){
        try {
            serverSocket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
