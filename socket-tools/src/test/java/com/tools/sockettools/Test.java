package com.tools.sockettools;

import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args){
        Test test = new Test();
        //test.testCreateServer();
        test.testsendRespons();
        //test.teststopServer();
        Integer integer = (Integer)10;
        Long  l = new Long(10);
    }

    public void testCreateServer(){
        RestTemplate restTemplate = new RestTemplate();

        Map<String,Object> config = new HashMap<>();
        /*config.put("Address","127.0.0.1:20101");
        config.put("RecvStrategy","0");
        config.put("HeadLen","4");
        config.put("LenCode","ASCII");
        config.put("ConnectionTimeout","600000");*/
        config.put("port", "20100");
        config.put("id", "001");
        byte[] bytes = restTemplate.postForObject("http://127.0.0.1:9001/createServer",config,byte[].class);
    }
    public void testsendRespons(){
        RestTemplate restTemplate = new RestTemplate();

        Map<String,Object> config = new HashMap<>();
        /*config.put("Address","127.0.0.1:20101");
        config.put("sendData","0000");*/
        config.put("sendData", "suceess...");
        config.put("id", "001");

        byte[] bytes = restTemplate.postForObject("http://127.0.0.1:9001/sendRespons",config,byte[].class);
    }

    public void teststopServer(){
        RestTemplate restTemplate = new RestTemplate();

        Map<String,Object> config = new HashMap<>();
        config.put("Address","127.0.0.1:20101");
        byte[] bytes = restTemplate.postForObject("http://127.0.0.1:9001/stopServer",config,byte[].class);
    }
}
