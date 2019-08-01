package com.tools.sockettools.http.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;

import java.util.Objects;
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
