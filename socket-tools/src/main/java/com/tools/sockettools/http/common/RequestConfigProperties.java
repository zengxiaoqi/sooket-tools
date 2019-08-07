package com.tools.sockettools.http.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@ConfigurationProperties("http.request")
@Data
public class RequestConfigProperties {
    /**
     * The maximum number of total connections the proxy can hold open to backends.
     */
    private int maxTotalConnections = 200;
    /**
     * The maximum number of connections that can be used by a single route.
     */
    private int maxPerRouteConnections = 20;
    /**
     * The socket timeout in millis. Defaults to 10000.
     */
    private int socketTimeoutMillis = 10000;
    /**
     * The connection timeout in millis. Defaults to 2000.
     */
    private int connectTimeoutMillis = 2000;
    /**
     * The timeout in milliseconds used when requesting a connection
     * from the connection manager. Defaults to -1, undefined use the system default.
     */
    private int connectionRequestTimeoutMillis = -1;
    /**
     * The lifetime for the connection pool.
     */
    private long timeToLive = -1;
    /**
     * The time unit for timeToLive.
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * Headers that are generally expected to be added by Spring Security, and hence often
     * duplicated if the proxy and the backend are secured with Spring. By default they
     * are added to the ignored headers if Spring Security is present and ignoreSecurityHeaders = true.
     */
    public static final List<String> SECURITY_HEADERS = Arrays.asList("Pragma",
            "Cache-Control", "X-Frame-Options", "X-Content-Type-Options",
            "X-XSS-Protection", "Expires");
    /**
     * Names of HTTP headers to ignore completely (i.e. leave them out of downstream
     * requests and drop them from downstream responses).
     */
    private Set<String> ignoredHeaders = new LinkedHashSet<>(
            Arrays.asList("access-control-allow-credentials", "access-control-allow-origin"));
    /**
     * List of sensitive headers that are not passed to downstream requests. Defaults to a
     * "safe" set of headers that commonly contain user credentials. It's OK to remove
     * those from the list if the downstream service is part of the same system as the
     * proxy, so they are sharing authentication data. If using a physical URL outside
     * your own domain, then generally it would be a bad idea to leak user credentials.
     */
    private Set<String> sensitiveHeaders = new LinkedHashSet<>(
            Arrays.asList("Cookie", "Set-Cookie", "Authorization"));

    /*public Set<String> getIgnoredHeaders() {
        Set<String> ignoredHeaders = new LinkedHashSet<>(this.ignoredHeaders);
        if (ClassUtils.isPresent(
                "org.springframework.security.config.annotation.web.WebSecurityConfigurer",
                null) && Collections.disjoint(ignoredHeaders, SECURITY_HEADERS)) {
            // Allow Spring Security in the gateway to control these headers
            ignoredHeaders.addAll(SECURITY_HEADERS);
        }
        return ignoredHeaders;
    }

    public void setIgnoredHeaders(Set<String> ignoredHeaders) {
        this.ignoredHeaders.addAll(ignoredHeaders);
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(maxTotalConnections, maxPerRouteConnections, socketTimeoutMillis, connectTimeoutMillis,
                connectionRequestTimeoutMillis, timeToLive, timeUnit);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("maxTotalConnections", maxTotalConnections)
                .append("maxPerRouteConnections", maxPerRouteConnections)
                .append("socketTimeoutMillis", socketTimeoutMillis)
                .append("connectTimeoutMillis", connectTimeoutMillis)
                .append("connectionRequestTimeoutMillis", connectionRequestTimeoutMillis)
                .append("timeToLive", timeToLive)
                .append("timeUnit", timeUnit)
                .toString();
    }
}
