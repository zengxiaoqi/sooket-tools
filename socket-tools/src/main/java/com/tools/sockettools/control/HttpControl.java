package com.tools.sockettools.control;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;

import org.apache.commons.io.IOUtils;
@Slf4j
@RestController
public class HttpControl {

    @RequestMapping(name="/httpRequest",method = RequestMethod.POST)
    public ResponseEntity<byte[]> httpRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ResponseEntity<byte[]> entity = null;

        printHttpHeaderLog(request);
        printHttpParamLog(request);
        try {
            printHttpBody(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        entity = new ResponseEntity<byte[]>(HttpStatus.OK);

        //转发http请求
        //参考spring cloud zuul SimpleHostRoutingFilter.run 请求转发实现
        /*try {
            //转发
            //request.getRequestDispatcher("http://127.0.0.1:9001/createServer").forward(request,response);
            //重定向
            //response.sendRedirect("http://127.0.0.1:9001/createServer");

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return entity;
    }
    /*public ResponseEntity<byte[]> httpRequest(@RequestBody Map<String,Object> httpBody,HttpServletRequest request){
        ResponseEntity<byte[]> entity = null;

        log.info("headers: "+ httpBody.get("headers").toString());
        log.info("params: "+httpBody.get("params").toString());
        printHttpHeaderLog(request);
        printHttpParamLog(request);
        //printHttpBody(request);

        entity = new ResponseEntity<byte[]>(HttpStatus.OK);

        return entity;
    }*/

    public static void printHttpParamLog(HttpServletRequest request) {
        log.info("请求类型：【{}】", request.getMethod());
        StringBuilder params = new StringBuilder("?");
        Enumeration<String> names = request.getParameterNames();
        /*if( request.getMethod().equals("GET") )*/ {
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                params.append(name);
                params.append("=");
                params.append(request.getParameter(name));
                params.append("&");
            }
            log.info("请求参数：【{}】", params.toString());
            return;
        }
        //return;
    }

    public static void printHttpHeaderLog(HttpServletRequest request) {
        StringBuilder params = new StringBuilder("");
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = (String) headers.nextElement();
            String value = request.getHeader(name);
            params.append("HEADER:: > ");
            params.append(name);
            params.append(":");
            params.append(value);
            params.append("\n");
        }
        log.info(params.toString());
    }

    public static void printHttpBody(HttpServletRequest request) throws UnsupportedEncodingException {
        byte buffer[]=getRequestBytes(request);
        String charEncoding=request.getCharacterEncoding();
        if(charEncoding==null){
            charEncoding="UTF-8";
        }
        String body= new String(buffer,charEncoding);
        log.info("BODY:: >{}" ,body);
    }

    /**
     * Get request query string
	 * @param request
	 * @return   byte[]
	 */
    public static byte[] getRequestBytes(HttpServletRequest request){
        int contentLength = request.getContentLength();
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {
            try {

                int readlen = request.getInputStream().read(buffer, i, contentLength - i);
                if (readlen == -1) {
                    break;
                }
                i += readlen;
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            } finally {
                // logger.info("Json Request:" + requestPacket);
            }
        }
        return buffer;
    }
}
