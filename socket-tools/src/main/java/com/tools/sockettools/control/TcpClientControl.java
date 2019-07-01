package com.tools.sockettools.control;

import com.tools.sockettools.tcp.client.Client;
import com.tools.sockettools.tcp.start.Server;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@RestController
public class TcpClientControl {
    private Map<String, ServerSocket> adapterMap = new HashMap<>();
    private Map<String, Selector> selectionMap = new HashMap<>();
    private List<Map<String,Object>> serverList = new ArrayList<>();
    //private Set<Map<String,Object>> serverList = new HashSet();


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

    @RequestMapping(value="/sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public String sendMsg(@RequestBody Map<String,Object> config) {
        Client client = new Client();
        Socket socket = Client.connectMap.get((String)config.get("id"));
        String rspMsg = client.sendMsg(socket,(String)config.get("sendMsg"));

        return rspMsg;
    }

}
