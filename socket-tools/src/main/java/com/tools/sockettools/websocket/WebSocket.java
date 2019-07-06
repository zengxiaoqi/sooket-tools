package com.tools.sockettools.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{shopId}")
public class WebSocket {
    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。
    private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<WebSocket>();
    //Session
    public static Map<String,Session> sessionPool = new HashMap<>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    @OnOpen
    public void onOpen(Session session, @PathParam(value="shopId")String shopId) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(shopId, session);
        System.out.println("【websocket消息】有新的连接，总数为:"+webSockets.size());
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        System.out.println("【websocket消息】连接断开，总数为:"+webSockets.size());
    }

    @OnMessage
    public  void onMessage(String message) {
        System.out.println("【websocket消息】收到客户端消息:"+message);
    }

    // 此为广播消息
    public static void sendAllMessage(String message) {
        for(WebSocket webSocket : webSockets) {
            System.out.println("【websocket消息】广播消息:"+message);
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息
    public static void sendOneMessage(String shopId, String message) {
        Session session = sessionPool.get(shopId);
        if (session != null) {
            try {
                System.out.println("【websocket消息】发送消息:"+message);
                synchronized(session) {
                    //session.getAsyncRemote().sendText(message);
                    session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendObject(String shopId, Object obj) {
        Session session = sessionPool.get(shopId);
        if (session != null) {
            try {
                System.out.println("【websocket消息】发送消息:"+ obj.toString());
                session.getAsyncRemote().sendObject(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
