package com.tools.sockettools.http.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientConnectionManagerFactory;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientFactory;
import org.springframework.cloud.commons.httpclient.DefaultApacheHttpClientConnectionManagerFactory;
import org.springframework.cloud.commons.httpclient.DefaultApacheHttpClientFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
@Data
@Component
public class SimpleHostRouting {
    private boolean forceOriginalQueryStringEncoding = true;

    private ProxyRequestHelper helper;

    private CloseableHttpClient httpClient;

    private RequestConfigProperties properties;
    private ApacheHttpClientFactory httpClientFactory;
    private HttpClientConnectionManager connectionManager;
    private ApacheHttpClientConnectionManagerFactory connectionManagerFactory;

    public SimpleHostRouting(){
        properties = new RequestConfigProperties();
        helper = new ProxyRequestHelper();
        httpClientFactory = new DefaultApacheHttpClientFactory();
        httpClient = newClient();
        connectionManagerFactory = new DefaultApacheHttpClientConnectionManagerFactory();
        this.connectionManager = connectionManagerFactory.newConnectionManager(
                true,
                this.properties.getMaxTotalConnections(),
                this.properties.getMaxPerRouteConnections(),
                this.properties.getTimeToLive(), this.properties.getTimeUnit(),
                null);
    }

    protected CloseableHttpClient newClient() {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(this.properties.getConnectionRequestTimeoutMillis())
                .setSocketTimeout(this.properties.getSocketTimeoutMillis())
                .setConnectTimeout(this.properties.getConnectTimeoutMillis())
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
        return httpClientFactory.createBuilder().
                setDefaultRequestConfig(requestConfig).
                setConnectionManager(this.connectionManager).disableRedirectHandling().build();
    }

    private String getVerb(HttpServletRequest request) {
        String sMethod = request.getMethod();
        return sMethod.toUpperCase();
    }

    protected InputStream getRequestBody(HttpServletRequest request) {
        InputStream requestEntity = null;
        try {
            requestEntity = request.getInputStream();
        }
        catch (IOException ex) {
            log.error("error during getRequestBody", ex);
        }
        return requestEntity;
    }
    private HttpHost getHttpHost(URL host) {
        HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(), host.getProtocol());
        return httpHost;
    }
    // Get the header value as a long in order to more correctly proxy very large requests
    protected long getContentLength(HttpServletRequest request) {
        return request.getContentLengthLong();
    }
    private String getEncodedQueryString(HttpServletRequest request) {
        String query = request.getQueryString();
        return (query != null) ? "?" + query : "";
    }
    private Header[] convertHeaders(MultiValueMap<String, String> headers) {
        List<Header> list = new ArrayList<>();
        for (String name : headers.keySet()) {
            for (String value : headers.get(name)) {
                list.add(new BasicHeader(name, value));
            }
        }
        return list.toArray(new BasicHeader[0]);
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

    private CloseableHttpResponse forward(CloseableHttpClient httpclient, String verb,
                                          String uri, HttpServletRequest request, MultiValueMap<String, String> headers,
                                          MultiValueMap<String, String> params, InputStream requestEntity)
            throws Exception {
        Map<String, Object> info = this.helper.debug(verb, uri, headers, params, requestEntity);
        URL host = new URL(uri);
        HttpHost httpHost = getHttpHost(host);
        //uri = StringUtils.cleanPath((host.getPath() + uri).replaceAll("/{2,}", "/")); //uri全路径
        long contentLength = getContentLength(request);

        ContentType contentType = null;

        if (request.getContentType() != null) {
            contentType = ContentType.parse(request.getContentType());
        }

        InputStreamEntity entity = new InputStreamEntity(requestEntity, contentLength,
                contentType);

        /*解决异常：Caused by: org.apache.http.ProtocolException: Content-Length header already present*/
        headers.remove("Content-Length");
        headers.remove("DEST_URL");
        HttpRequest httpRequest = buildHttpRequest(verb, uri, entity, headers, params, request);
        try {
            log.debug(httpHost.getHostName() + " " + httpHost.getPort() + " " + httpHost.getSchemeName());
            CloseableHttpResponse httpResponse = forwardRequest(httpclient, httpHost, httpRequest);
            this.helper.appendDebug(info, httpResponse.getStatusLine().getStatusCode(), revertHeaders(httpResponse.getAllHeaders()));
            return httpResponse;
        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            // httpclient.getConnectionManager().shutdown();
        }
    }

    protected HttpRequest buildHttpRequest(String verb, String uri,
                                           InputStreamEntity entity, MultiValueMap<String, String> headers,
                                           MultiValueMap<String, String> params, HttpServletRequest request) {
        HttpRequest httpRequest;
        String uriWithQueryString = uri + (this.forceOriginalQueryStringEncoding
                ? getEncodedQueryString(request) : this.helper.getQueryString(params));

        switch (verb.toUpperCase()) {
            case "POST":
                HttpPost httpPost = new HttpPost(uriWithQueryString);
                httpRequest = httpPost;
                httpPost.setEntity(entity);
                break;
            case "PUT":
                HttpPut httpPut = new HttpPut(uriWithQueryString);
                httpRequest = httpPut;
                httpPut.setEntity(entity);
                break;
            case "PATCH":
                HttpPatch httpPatch = new HttpPatch(uriWithQueryString);
                httpRequest = httpPatch;
                httpPatch.setEntity(entity);
                break;
            case "DELETE":
                BasicHttpEntityEnclosingRequest entityRequest = new BasicHttpEntityEnclosingRequest(
                        verb, uriWithQueryString);
                httpRequest = entityRequest;
                entityRequest.setEntity(entity);
                break;
            default:
                httpRequest = new BasicHttpRequest(verb, uriWithQueryString);
                log.debug(uriWithQueryString);
        }

        httpRequest.setHeaders(convertHeaders(headers));
        return httpRequest;
    }

    private CloseableHttpResponse forwardRequest(CloseableHttpClient httpclient,
                                                 HttpHost httpHost, HttpRequest httpRequest) throws IOException {

        return httpclient.execute(httpHost, httpRequest);
    }


    public CloseableHttpResponse httpForwad(HttpServletRequest request){
        MultiValueMap<String, String> headers = this.helper.buildRequestHeaders(request);

        MultiValueMap<String, String> params = this.helper.buildRequestQueryParams(request);
        String verb = getVerb(request);
        InputStream requestEntity = getRequestBody(request);
        if (getContentLength(request) < 0) {
            log.error("报文长度小于0");
        }

        //String uri = this.helper.buildZuulRequestURI(request);
        String uri = request.getRequestURL().toString();
        //从请求头里面获取目的URL
        uri = request.getHeader("DEST_URL");
        try {

            CloseableHttpResponse response = forward(this.httpClient, verb, uri, request,
                    headers, params, requestEntity);
            return response;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.error("-------forward error---------------");
        }
        return null;
    }
}
