package com.tools.sockettools.control;

import com.tools.sockettools.common.util.ObjectMapUtil;
import com.tools.sockettools.tcp.start.Adapter;
import com.tools.sockettools.tcp.start.TcpConnShortServer;

import org.springframework.web.bind.annotation.*;

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
            TcpConnShortServer tcpConnShortServer = new TcpConnShortServer(config);
            Selector selector = tcpConnShortServer.start();
            adapterMap.put((String)config.get("Address"), tcpConnShortServer);
            selectionMap.put((String)config.get("Address"), selector);
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
        try {
            Adapter adapter = adapterMap.get(config.get("Address"));
            Selector selector = selectionMap.get(config.get("Address"));
            String rspData = (String)config.get("sendData");
            adapter.invoke(selector.selectedKeys().iterator().next(), rspData.getBytes());
            selector.selectedKeys().iterator().remove();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
