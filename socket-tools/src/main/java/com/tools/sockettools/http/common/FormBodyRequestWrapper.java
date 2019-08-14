package com.tools.sockettools.http.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.util.MultiValueMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FormBodyRequestWrapper extends Servlet30RequestWrapper {

    private HttpServletRequest request;

    private byte[] contentData;

    private MediaType contentType;

    private int contentLength;

    public FormBodyRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public String getContentType() {
        if (this.contentData == null) {
            buildContentData();
        }
        return this.contentType.toString();
    }

    @Override
    public int getContentLength() {
        if (super.getContentLength() <= 0) {
            return super.getContentLength();
        }
        if (this.contentData == null) {
            buildContentData();
        }
        return this.contentLength;
    }

    @Override
    public long getContentLengthLong() {
        return getContentLength();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.contentData == null) {
            buildContentData();
        }
        return new ServletInputStreamWrapper(this.contentData);
    }

    private synchronized void buildContentData() {
        try {
            MultiValueMap<String, Object> builder = RequestContentDataExtractor.extract(this.request);
            FormBodyRequestWrapper.FormHttpOutputMessage data = new FormBodyRequestWrapper.FormHttpOutputMessage();

            this.contentType = MediaType.valueOf(this.request.getContentType());
            data.getHeaders().setContentType(this.contentType);
            FormHttpMessageConverter formHttpMessageConverter = new AllEncompassingFormHttpMessageConverter();
            formHttpMessageConverter.write(builder, this.contentType, data);
            // copy new content type including multipart boundary
            this.contentType = data.getHeaders().getContentType();
            this.contentData = data.getInput();
            this.contentLength = this.contentData.length;
        }
        catch (Exception e) {
            throw new IllegalStateException("Cannot convert form data", e);
        }
    }

    private class FormHttpOutputMessage implements HttpOutputMessage {

        private HttpHeaders headers = new HttpHeaders();
        private ByteArrayOutputStream output = new ByteArrayOutputStream();

        @Override
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override
        public OutputStream getBody() throws IOException {
            return this.output;
        }

        public byte[] getInput() throws IOException {
            this.output.flush();
            return this.output.toByteArray();
        }

    }

}