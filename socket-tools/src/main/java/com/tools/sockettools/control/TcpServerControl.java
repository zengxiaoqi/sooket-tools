package com.tools.sockettools.control;

import com.tools.sockettools.common.util.DateUtil;
import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.entity.ServerInfo;
import com.tools.sockettools.tcp.server.ServerThread;
import com.tools.sockettools.tcp.start.Server;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.util.*;

/**
 * @author Administrator
 */
@RestController
public class TcpServerControl {
    private Map<String, ServerSocket> adapterMap = new HashMap<>();
    private Map<String, Selector> selectionMap = new HashMap<>();
    //private List<Map<String,Object>> serverList = new ArrayList<>();
    private Map<String, ServerInfo> serverList = new HashMap<String, ServerInfo>();   //根据ID 保存服务端状态
    //private Set<Map<String,Object>> serverList = new HashSet();
    public static List<NodeTree> nodeTreeList = new ArrayList<NodeTree>();

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

            ServerInfo serverInfo = new ServerInfo();
            //保存监听返回的 ServerSocket ，供后续关闭使用
            serverInfo.setId(id);
            serverInfo.setServerSocket(finalServerSocket);
            serverInfo.setStatus("open");

            NodeTree nodeTree = new NodeTree();
            nodeTree.setId(id);
            nodeTree.setLeaf(false);


            if(serverList.get(id) == null) {
                //新建服务
                serverList.put(id, serverInfo);
                nodeTreeList.add(nodeTree);
            }else {
                //重启监听
                serverList.get(id).setStatus("open");
            }

            //returnResult.setData(serverList);
            returnResult.setData(nodeTreeList);
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("新建服务失败："+e.getMessage());
        }

        return returnResult;
    }

    @RequestMapping(value="/stopServer",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult stopServer(@RequestParam("id") String id) {
        ServerSocket socket = null;
        ReturnResult returnResult = new ReturnResult();
        try {
            socket = serverList.get(id).getServerSocket();
            if(socket!=null) {
                socket.close();
                returnResult.setSuccess(true);
            }else {
                returnResult.setSuccess(false);
                returnResult.setMessage("关闭服务失败：id["+id+"]不存在");
            }
            serverList.get(id).setStatus("close");

            returnResult.setData(serverList);
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("关闭服务失败："+e.getMessage());
        }

        return returnResult;
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
            ServerThread.socketMap.get(socket).append("["+DateUtil.getNowStrDate()+"]"+"发送数据: ");
            ServerThread.socketMap.get(socket).append((String)config.get("sendData"));
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
    public ReturnResult getServerList(@RequestParam("id") String id) {
        ReturnResult returnResult = new ReturnResult();

        returnResult.setSuccess(true);
        returnResult.setData(serverList.get(id));
        return returnResult;
    }

    @RequestMapping(value="/getRcvMsg",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getRcvMsg(@RequestParam("id") String id) {
        ReturnResult returnResult = new ReturnResult();
        Socket socket = null;
        socket = Server.connectMap.get(id);
        StringBuffer stringBuffer = ServerThread.socketMap.get(socket);

        returnResult.setSuccess(true);
        returnResult.setData(stringBuffer.toString());
        return returnResult;
    }
}
