package com.tools.sockettools;

import com.tools.sockettools.control.SocketToolControl;
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
        SocketToolControl socketToolControl = new SocketToolControl();
        Map<String, Object> config = new HashMap<>();
        config.put("port", "20100");
        config.put("id", "001");
        socketToolControl.createServer(config);
    }

    @Test
    public void testSend(){
        SocketToolControl socketToolControl = new SocketToolControl();
        Map<String, Object> config = new HashMap<>();
        config.put("sendData", "suceess...");
        config.put("id", "001");
        socketToolControl.sendRespons(config);
    }
}
