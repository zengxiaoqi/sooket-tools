package com.tools.sockettools.control;

import com.tools.sockettools.common.util.ObjectMapUtil;
import com.tools.sockettools.tcp.start.Adapter;
import com.tools.sockettools.tcp.start.Server;
import com.tools.sockettools.tcp.start.TcpConnShortServer;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */
@RestController
public class SocketToolControl {
    private Map<String, Adapter> adapterMap = new HashMap<>();
    private Map<String, Selector> selectionMap = new HashMap<>();

    @RequestMapping(value="/createServer",method = RequestMethod.POST)
    @ResponseBody
    public byte[] createServer(@RequestBody Map<String,Object> config) {
        try {
            //Map<String, Object> config = ObjectMapUtil.objectToMap(object);
            /*TcpConnShortServer tcpConnShortServer = new TcpConnShortServer(config);
            Selector selector = tcpConnShortServer.start();
            adapterMap.put((String)config.get("Address"), tcpConnShortServer);
            selectionMap.put((String)config.get("Address"), selector);*/
            Server server = new Server();
            ServerSocket serverSocket = server.createServer(config);
            server.start(serverSocket);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value="/stopServer",method = RequestMethod.POST)
    @ResponseBody
    public byte[] stopServer(@RequestBody Map<String,Object> config) {
        try {
            Adapter adapter = adapterMap.get(config.get("Address"));
            adapter.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value="/sendRespons",method = RequestMethod.POST)
    @ResponseBody
    public byte[] sendRespons(@RequestBody Map<String,Object> config) {
        OutputStream os=null;
        PrintWriter pw=null;
        Socket socket = null;
        try {
            socket = Server.connectMap.get((String)config.get("id"));
            os = socket.getOutputStream();
            pw = new PrintWriter(os);
            pw.write((String)config.get("sendData"));
            pw.flush();
            /*Adapter adapter = adapterMap.get(config.get("Address"));
            Selector selector = selectionMap.get(config.get("Address"));
            String rspData = (String)config.get("sendData");
            adapter.invoke(selector.selectedKeys().iterator().next(), rspData.getBytes());
            selector.selectedKeys().iterator().remove();*/
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            //关闭资源
            try {
                if(pw!=null)
                    pw.close();
                if(os!=null)
                    os.close();
                if(socket!=null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
