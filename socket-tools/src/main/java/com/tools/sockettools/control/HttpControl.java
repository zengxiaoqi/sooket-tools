package com.tools.sockettools.control;

import com.tools.sockettools.http.common.HTTPRequestUtils;
import com.tools.sockettools.http.common.ProxyRequestHelper;
import com.tools.sockettools.http.common.SimpleHostRouting;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

@Slf4j
@RestController
@RequestMapping(value="/http")
public class HttpControl {

    private SimpleHostRouting simpleHostRouting;

    @RequestMapping(value="/httpRequest")
    public void httpRequest(HttpServletRequest request, HttpServletResponse httpServletResponse) throws ServletException {
        ResponseEntity<byte[]> entity = null;

        printHttpHeaderLog(request);
        printHttpParamLog(request);
        /*try {
            printHttpBody(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        simpleHostRouting = new SimpleHostRouting();
        CloseableHttpResponse response = null;
        response = simpleHostRouting.httpForwad(request);

        HttpResponse response1 = null;
        log.info("stastus: {}", response.getStatusLine().getStatusCode());
        //Header[] headers = response.getAllHeaders();
        /*try {
            printRspBody(response);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            //httpServletResponse.getOutputStream().write("123".getBytes());
            setResponse(httpServletResponse,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setResponse(HttpServletResponse httpServletResponse,HttpResponse response) throws Exception {
        InputStream is = null;
        OutputStream outStream = httpServletResponse.getOutputStream();
        httpServletResponse.setStatus(response.getStatusLine().getStatusCode());
        MultiValueMap<String, String> headers = revertHeaders(response.getAllHeaders());
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            String name = header.getKey();
            ProxyRequestHelper helper = new ProxyRequestHelper();
            for (String value : header.getValue()) {
                if(helper.isIncludedHeader(name)){
                    httpServletResponse.addHeader(name,value);
                }
                log.info("RESPONSE HEADER: >{}:{}", name, value);
                if (name.equalsIgnoreCase(HttpHeaders.CONTENT_ENCODING)
                        && HTTPRequestUtils.getInstance().isGzipped(value)) {
                    //isOriginResponseGzipped = true;
                    httpServletResponse.setHeader("Content-Encoding", "gzip");
                    is = handleGzipStream(is);
                }
                if (name.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                    httpServletResponse.setContentLength(Integer.parseInt(value));
                }
            }


        }

        is = response.getEntity().getContent();
        if (is!=null) {
            writeResponse(is, outStream);
        }
    }
    protected InputStream handleGzipStream(InputStream in) throws Exception {
        // Record bytes read during GZip initialization to allow to rewind the stream if needed
        //
        RecordingInputStream stream = new RecordingInputStream(in);
        try {
            return new GZIPInputStream(stream);
        }
        catch (java.util.zip.ZipException | java.io.EOFException ex) {

            if (stream.getBytesRead()==0) {
                // stream was empty, return the original "empty" stream
                return in;
            }
            else {
                // reset the stream and assume an unencoded response
                log.warn(
                        "gzip response expected but failed to read gzip headers, assuming unencoded response for request ");
                stream.reset();
                return stream;
            }
        }
        finally {
            stream.stopRecording();
        }
    }
    private void writeResponse(InputStream zin, OutputStream out) throws Exception {
        byte[] bytes = new byte[1024];
        int bytesRead = -1;
        while ((bytesRead = zin.read(bytes)) != -1) {
            log.info("应答信息："+ new String(bytes));
            out.write(bytes, 0, bytesRead);
        }
        out.flush();
    }
    private MultiValueMap<String, String> revertHeaders(Header[] headers) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (Header header : headers) {
            String name = header.getName();
            if (!map.containsKey(name)) {
                map.put(name, new ArrayList<String>());
            }
            map.get(name).add(header.getValue());
        }
        return map;
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

    public static void printRspBody(HttpResponse response) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
            sb.append(line + NL);
        }
        in.close();
        String content = sb.toString();
        log.info("应答报文：【{}】", content);
    }

    /**
     * InputStream recording bytes read to allow for a reset() until recording is stopped.
     */
    private static class RecordingInputStream extends InputStream {

        private InputStream delegate;
        private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        public RecordingInputStream(InputStream delegate) {
            super();
            this.delegate = Objects.requireNonNull(delegate);
        }

        @Override
        public int read() throws IOException {
            int read = delegate.read();

            if (buffer!=null && read!=-1) {
                buffer.write(read);
            }

            return read;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int read = delegate.read(b, off, len);

            if (buffer!=null && read!=-1) {
                buffer.write(b, off, read);
            }

            return read;
        }

        @Override
        public void reset() {
            if (buffer==null) {
                throw new IllegalStateException("Stream is not recording");
            }

            this.delegate = new SequenceInputStream(new ByteArrayInputStream(buffer.toByteArray()), delegate);
            this.buffer = new ByteArrayOutputStream();
        }

        public int getBytesRead() {
            return (buffer==null)?-1:buffer.size();
        }

        public void stopRecording() {
            this.buffer = null;
        }

        @Override
        public void close() throws IOException {
            this.delegate.close();
        }
    }
}
