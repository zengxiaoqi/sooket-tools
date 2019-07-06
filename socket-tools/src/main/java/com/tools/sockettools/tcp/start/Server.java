package com.tools.sockettools.tcp.start;

import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.tcp.server.RecvThread;
import com.tools.sockettools.tcp.server.StaticStore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于TCP协议的Socket通信
 * 服务器端
 */
@Data
@Slf4j
public class Server implements Runnable{
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private String id;
    private int port ;
    private String encode;
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public Server(){}

    public Server(Map config) throws IOException {
        port = Integer.parseInt((String)config.get("port"));
        id = (String)config.get("id");
        encode = (String)config.get("encode");

        pool = Executors.newFixedThreadPool(StaticStore.MAX_ACCPT_POOL_SIZE);

        //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
        serverSocket=new ServerSocket(port);

        System.out.println("***创建监听成功，端口:"+ port);
       /**
         * 保存 pool信息，供后续关闭使用
         */
        StaticStore.accptPoolMap.put(id, pool);

        StaticStore.serverMap.put(id, true);
    }


    @Override
    public void run() {
        try {
            Socket socket=null;
            //记录客户端的数量
            int count=0;
            System.out.println("***服务器即将启动，等待客户端的连接***");
            //循环监听等待客户端的连接
            while(StaticStore.serverMap.get(id)){
                //serverSocket.setSoTimeout(3000);
                socket = serverSocket.accept();

                pool.execute(new RecvThread(socket,encode));

                String childId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                StaticStore.connectMap.put(childId, socket);

                NodeTree nodeTree = new NodeTree();
                nodeTree.setParentId(id);
                nodeTree.setId(childId);
                nodeTree.setLeaf(true);
                StaticStore.addChildNodeTree(id, nodeTree);

                count++;//统计客户端的数量
                System.out.println("客户端的数量："+count);
            }
            System.out.println("---------监听结束，清理线程---------");
            StaticStore.deleteChildByParentId(id);
            StaticStore.socketMap.remove(socket);

            //shutdownAndAwaitTermination(pool);
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void stop(String id, ServerSocket serverSocket){
        if (serverSocket != null){
            //shutdownAndAwaitTermination(pool);
            try {

                //serverSocket.close();
                Socket socket = new Socket("127.0.0.1",serverSocket.getLocalPort());
                StaticStore.serverMap.put(id,false);
                socket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdownAndAwaitTermination(ExecutorService pool) {
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
