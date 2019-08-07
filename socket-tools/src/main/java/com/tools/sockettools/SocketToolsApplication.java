package com.tools.sockettools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class SocketToolsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketToolsApplication.class, args);
    }

}
