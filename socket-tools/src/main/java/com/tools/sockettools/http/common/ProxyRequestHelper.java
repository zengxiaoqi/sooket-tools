package com.tools.sockettools.http.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriTemplate;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class ProxyRequestHelper {

    public MultiValueMap<String, String> buildRequestHeaders(HttpServletRequest request) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                //排除跨域头信息
                if(isIncludedHeader(name)){
                    Enumeration<String> values = request.getHeaders(name);
                    while (values.hasMoreElements()) {
                        String value = values.nextElement();
                        headers.add(name, value);
                    }
                }
            }
        }

        if(!headers.containsKey(HttpHeaders.ACCEPT_ENCODING)) {
            headers.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
        }
        return headers;
    }

    public MultiValueMap<String, String> buildRequestQueryParams(
            HttpServletRequest request) {
        Map<String, List<String>> map = HTTPRequestUtils.getInstance().getQueryParams(request);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (map == null) {
            return params;
        }
        for (String key : map.keySet()) {
            for (String value : map.get(key)) {
                params.add(key, value);
            }
        }
        return params;
    }
    public String buildZuulRequestURI(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri;
    }

    public Map<String, Object> debug(String verb, String uri,
                                     MultiValueMap<String, String> headers, MultiValueMap<String, String> params,
                                     InputStream requestEntity) throws IOException {
        Map<String, Object> info = new LinkedHashMap<>();
        return info;
    }

    protected boolean shouldDebugBody(HttpServletRequest request) {
        if (request == null || request.getContentType() == null) {
            return true;
        }
        return !request.getContentType().toLowerCase().contains("multipart");
    }

    public void appendDebug(Map<String, Object> info, int status,
                            MultiValueMap<String, String> headers) {
    }

    /**
     * Get url encoded query string. Pay special attention to single parameters with no values
     * and parameter names with colon (:) from use of UriTemplate.
     * @param params Un-encoded request parameters
     * @return
     */
    public String getQueryString(MultiValueMap<String, String> params) {
        if (params.isEmpty()) {
            return "";
        }
        StringBuilder query = new StringBuilder();
        Map<String, Object> singles = new HashMap<>();
        for (String param : params.keySet()) {
            int i = 0;
            for (String value : params.get(param)) {
                query.append("&");
                query.append(param);
                if (!"".equals(value)) { // don't add =, if original is ?wsdl, output is not ?wsdl=
                    String key = param;
                    // if form feed is already part of param name double
                    // since form feed is used as the colon replacement below
                    if (key.contains("\f")) {
                        key = (key.replaceAll("\f", "\f\f"));
                    }
                    // colon is special to UriTemplate
                    if (key.contains(":")) {
                        key = key.replaceAll(":", "\f");
                    }
                    key = key + i;
                    singles.put(key, value);
                    query.append("={");
                    query.append(key);
                    query.append("}");
                }
                i++;
            }
        }

        UriTemplate template = new UriTemplate("?" + query.toString().substring(1));
        return template.expand(singles).toString();
    }

    public boolean isIncludedHeader(String headerName) {
        String name = headerName.toLowerCase();
        RequestConfigProperties requestConfigProperties = new RequestConfigProperties();
        if (requestConfigProperties.getIgnoredHeaders().size() > 0) {
            if (requestConfigProperties.getIgnoredHeaders().contains(name)) {
                return false;
            }
        }
        switch (name) {
            case "host":
            case "connection":
            case "content-length":
            case "content-encoding":
            case "server":
            case "transfer-encoding":
            case "x-application-context":
                return false;
            default:
                return true;
        }
    }

}
