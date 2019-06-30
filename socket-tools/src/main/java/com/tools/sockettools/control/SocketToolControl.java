package com.tools.sockettools.control;

import com.tools.sockettools.common.util.ObjectMapUtil;
import com.tools.sockettools.tcp.client.Client;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@RestController
public class SocketToolControl {
    private Map<String, Adapter> adapterMap = new HashMap<>();
    private Map<String, Selector> selectionMap = new HashMap<>();
    private List<Map<String,Object>> serverList = new ArrayList<>();

    @RequestMapping(value="/createServer",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult createServer(@RequestBody Map<String,Object> config) {
        ReturnResult returnResult = new ReturnResult();
        try {
            //Map<String, Object> config = ObjectMapUtil.objectToMap(object);
            /*TcpConnShortServer tcpConnShortServer = new TcpConnShortServer(config);
            Selector selector = tcpConnShortServer.start();
            adapterMap.put((String)config.get("Address"), tcpConnShortServer);
            selectionMap.put((String)config.get("Address"), selector);*/
            Server server = new Server();
            ServerSocket finalServerSocket = server.createServer(config);
            Thread thread = new Thread(){
                @Override
                public void run(){
                    server.start(finalServerSocket);
                }
            };
            thread.start();
            returnResult.setSuccess(true);

            config.put("status", "open");
            serverList.add(config);
            returnResult.setData(serverList);
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("新建服务失败："+e.getMessage());
        }

        return returnResult;
    }

    @RequestMapping(value="/createClient",method = RequestMethod.POST)
    @ResponseBody
    public byte[] createClient(@RequestBody Map<String,Object> config) {
        try {
            Client client = new Client();
            Socket socket = client.createClient(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value="/stopClient",method = RequestMethod.POST)
    @ResponseBody
    public byte[] stopClient(@RequestBody Map<String,Object> config) {
        Socket socket = null;
        try {
            socket = Client.connectMap.get((String)config.get("id"));
            if(socket!=null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value="/stopServer",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult stopServer(@RequestParam("id") String id) {
        Socket socket = null;
        ReturnResult returnResult = new ReturnResult();
        try {
            socket = Server.connectMap.get(id);
            if(socket!=null) {
                socket.close();
                returnResult.setSuccess(true);
            }
            for(Map<String,Object> server : serverList){
                if(server.get("id").equals(id)){
                    server.put("status", "close");
                }
            }
            returnResult.setData(serverList);
            /*Adapter adapter = adapterMap.get(config.get("Address"));
            adapter.stop();*/
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("关闭服务失败："+e.getMessage());
        }

        return returnResult;
    }
    @RequestMapping(value="/sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public String sendMsg(@RequestBody Map<String,Object> config) {
        Client client = new Client();
        Socket socket = Client.connectMap.get((String)config.get("id"));
        String rspMsg = client.sendMsg(socket,(String)config.get("sendMsg"));

        return rspMsg;
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
                /*if(socket!=null)
                    socket.close();*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @RequestMapping(value="/getServerList",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getServerList() {
        ReturnResult returnResult = new ReturnResult();

        returnResult.setSuccess(true);
        returnResult.setData(serverList);
        return returnResult;
    }

    @RequestMapping(value="/getRcvMsg",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getRcvMsg(@RequestParam("id") String id) {
        ReturnResult returnResult = new ReturnResult();

        returnResult.setSuccess(true);
        returnResult.setData("测试。。。");
        return returnResult;
    }
}
