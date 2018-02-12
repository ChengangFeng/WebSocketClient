package com.github.chengang.ibrary;

import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * webSocket连接配置
 *
 * @author FengChengang
 * @date 2018/2/12
 */

public class WebSocketProperty {
    /**
     * websocket地址
     */
    private String wsUrl;
    /**
     * okHttpClient
     */
    private OkHttpClient okHttpClient;
    /**
     * 超时重连的时间，已秒为单位，默认60s
     */
    private long timeout = 60;

    /**
     * 请求头设置，将已键值对的形式放到里面
     */
    private Map<String,String> headersMap;

    public WebSocketProperty() {
    }

    public WebSocketProperty(String wsUrl, OkHttpClient okHttpClient, long timeout, Map<String, String> headersMap) {
        this.wsUrl = wsUrl;
        this.okHttpClient = okHttpClient;
        this.timeout = timeout;
        this.headersMap = headersMap;
    }

    public String getWsUrl() {
        return wsUrl;
    }

    public WebSocketProperty setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
        return this;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public WebSocketProperty setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public WebSocketProperty setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public Map<String, String> getHeadersMap() {
        return headersMap;
    }

    public WebSocketProperty setHeadersMap(Map<String, String> headersMap) {
        this.headersMap = headersMap;
        return this;
    }
}
