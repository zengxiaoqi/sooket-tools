package com.tools.sockettools;

import com.tools.sockettools.control.TcpServerControl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SocketToolsApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testServer(){
        TcpServerControl tcpServerControl = new TcpServerControl();
        Map<String, Object> config = new HashMap<>();
        config.put("port", "20100");
        config.put("id", "001");
        tcpServerControl.createServer(config);
    }

    @Test
    public void testSend(){
        TcpServerControl tcpServerControl = new TcpServerControl();
        Map<String, Object> config = new HashMap<>();
        config.put("sendData", "suceess...");
        config.put("id", "001");
        tcpServerControl.sendRespons(config);
    }

}
