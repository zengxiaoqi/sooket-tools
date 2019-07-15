package com.tools.sockettools.control;

import com.tools.sockettools.entity.ClientInfo;
import com.tools.sockettools.tcp.client.Client;
import com.tools.sockettools.tcp.common.SendThread;
import com.tools.sockettools.common.StaticStore;
import org.springframework.web.bind.annotation.*;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author zengxq
 */
@RestController
public class TcpClientControl {

    @RequestMapping(value="/createClient",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult createClient(@RequestBody Map<String,Object> config) {
        ReturnResult returnResult = new ReturnResult();
        try {
            Client client = new Client();
            Socket socket = client.createClient(config);

            //创建接收线程
            client.recvMsg(socket,client.getEncode());
            //保存监听返回的 socket ，供后续关闭使用
            ClientInfo clientInfo = new ClientInfo();

            clientInfo.setId(client.getId());
            clientInfo.setIp(socket.getInetAddress().getHostAddress());
            clientInfo.setPort(socket.getLocalPort());
            clientInfo.setRemoteIp((String)config.get("ip"));
            clientInfo.setRemotePort(socket.getPort());
            clientInfo.setStatus(!socket.isClosed());
            clientInfo.setEncode((String)config.get("encode"));
            //保存客户端信息
            StaticStore.clientList.add(clientInfo);

            returnResult.setSuccess(true);
            returnResult.setData(StaticStore.clientList);
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("创建客户端失败...");
        }

        return returnResult;
    }

    @RequestMapping(value="/stopClient",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult stopClient(@RequestBody Map<String,Object> config) {
        String id = (String)config.get("id");
        Socket socket = StaticStore.connectMap.get(id);


        Client client = new Client();
        client.closeClient(socket);
        StaticStore.socketMap.remove(socket);
        //清理线程
        StaticStore.shutdownAndAwaitTermination(StaticStore.accptPoolMap.get(id));

        //更新clientList状态
        ClientInfo clientInfo = StaticStore.getClientInfoById(id);
        clientInfo.setStatus(!socket.isClosed());
        StaticStore.setClientInfoById(id,clientInfo);

        StaticStore.connectMap.put(id, socket);
        return ReturnResult.success(StaticStore.clientList);
    }

    @RequestMapping(value="/openClient",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult openClient(@RequestBody Map<String,Object> config) {
        String id = (String)config.get("id");
        //断开连接，删除ID数据
        Socket socket = StaticStore.connectMap.get(id);
        if(null != socket && !socket.isClosed()){
            stopClient(config);
        }
        StaticStore.connectMap.remove(id);
        StaticStore.accptPoolMap.remove(id);
        StaticStore.delClientInfoById(id);

        Map<String,Object> newConfig = new HashMap<>();
        newConfig.put("ip", config.get("remoteIp"));
        newConfig.put("port", config.get("remotePort"));
        newConfig.put("encode", config.get("encode"));
        createClient(newConfig);
        return ReturnResult.success(StaticStore.clientList);
    }

    @RequestMapping(value="/delClient",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult delClient(@RequestBody Map<String,Object> config) {
        String id = (String)config.get("id");
        //断开连接，删除ID数据
        Socket socket = StaticStore.connectMap.get(id);
        if(null != socket && !socket.isClosed()){
            stopClient(config);
        }
        StaticStore.connectMap.remove(id);
        StaticStore.accptPoolMap.remove(id);
        StaticStore.delClientInfoById(id);

        return ReturnResult.success(StaticStore.clientList);
    }

    @RequestMapping(value="/sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult sendMsg(@RequestBody Map<String,Object> config) {
        String id = (String)config.get("id");
        Socket socket = StaticStore.connectMap.get(id);
        ClientInfo clientInfo = StaticStore.getClientInfoById(id);

        ExecutorService pool = StaticStore.accptPoolMap.get(id);
        SendThread sendThread = new SendThread(socket,(String)config.get("sendMsg"),clientInfo.getEncode(),(Boolean) config.get("hexStr"));
        pool.execute(sendThread);

        return ReturnResult.success();
    }

    @RequestMapping(value="/getClientList",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getClientList() {
        return ReturnResult.success(StaticStore.clientList);
    }

}
