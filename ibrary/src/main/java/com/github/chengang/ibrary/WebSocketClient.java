package com.github.chengang.ibrary;

import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.TimeUnit;


import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * WebSocket连接工具类，依赖于 okhttp3 的 websocket
 *
 * @author 陈岗不姓陈
 * @date 2018/2/12
 */

public class WebSocketClient {

    private static final String TAG = "WebSocketClient";
    private static final String THREAD_MAIN = "main";
    private static final String OKHTTP_PACKAGE_NAME = "okhttp3.OkHttpClient";
    private static final boolean hasOkHttpDependency;
    private static WebSocketClient instance;
    private static WebSocket mWebSocket;
    private static final WebSocketProperty mWebsocketProperty;

    static {
        boolean hasDependency;
        try {
            Class.forName(OKHTTP_PACKAGE_NAME);
            hasDependency = true;
        } catch (ClassNotFoundException e) {
            hasDependency = false;
        }
        hasOkHttpDependency = hasDependency;
        mWebsocketProperty = new WebSocketProperty();
    }

    private WebSocketClient() {

    }

    /**
     * 获取websocket的连接属性
     *
     * @return websocket的连接属性
     */
    public static WebSocketProperty getConnectionProperty() {
        return mWebsocketProperty;
    }

    /**
     * 实例化IMSocketClient，保证全局只有一个实例
     *
     * @return IMSocketClient的实例
     */
    public static WebSocketClient getInstance() {
        if (instance == null) {
            synchronized (WebSocketClient.class) {
                if (instance == null) {
                    instance = new WebSocketClient();
                }
            }
        }
        return instance;
    }

    /**
     * 开启websocket服务
     * 订阅socket消息，则在需要的地方subscribe即可
     *
     * @return Flowable的服务
     */
    public Flowable<String> openSocketService() {
        propertiesCheck();
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                initWebSocket(emitter);
            }

            private void initWebSocket(final FlowableEmitter<String> emitter) {
                if (mWebSocket != null) {
                    shutDownSocket();
                    //降低重连频率
                    if (!THREAD_MAIN.equals(Thread.currentThread().getName())) {
                        SystemClock.sleep(10000);
                    }
                }
                mWebSocket = mWebsocketProperty.getOkHttpClient().newWebSocket(getRequest(), getWebSocketListener(emitter));
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .timeout(mWebsocketProperty.getTimeout(), TimeUnit.SECONDS)
                .retry();
    }

    /**
     * 实现websocket接口
     *
     * @param emitter emitter发射器
     * @return websocket listener
     */
    private WebSocketListener getWebSocketListener(final FlowableEmitter<String> emitter) {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.i(TAG, "websocket get -----------------> websocket onOpen");
                mWebSocket = webSocket;
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                Log.i(TAG, "websocket get -----------------> websocket onMessage: " + text);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                Log.i(TAG, "websocket get -----------------> websocket onClosing: " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.i(TAG, "websocket get -----------------> websocket onClosed: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                Log.i(TAG, "websocket get -----------------> websocket onFailure: " + t.toString());
                emitter.onError(t);
            }
        };
    }

    /**
     * 检查连接属性，一些必须配置项不能为空
     */
    private void propertiesCheck() {
        if (!hasOkHttpDependency) {
            throw new IllegalStateException("Must be dependency Okhttp");
        }
        if (TextUtils.isEmpty(mWebsocketProperty.getWsUrl())) {
            throw new IllegalStateException("websocket url must not be null");
        }
        if (mWebsocketProperty.getOkHttpClient() == null) {
            throw new IllegalStateException("okHttpClient must not be null");
        }
        if (mWebSocket != null) {
            throw new IllegalStateException("the websocket is running, please shut it down before open it again");
        }
    }

    /**
     * 获取http request
     *
     * @return http request
     */
    private Request getRequest() {
        Request.Builder builder = new Request.Builder()
                .url(mWebsocketProperty.getWsUrl());
        Map<String, String> headersMap = mWebsocketProperty.getHeadersMap();
        //设置请求头
        if (headersMap != null && headersMap.size() > 0) {
            for (Map.Entry<String, String> header : headersMap.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @return 是否发送成功
     */
    public boolean sendMessage(String message) {
        if (mWebSocket != null) {
            boolean sendResult = mWebSocket.send(message);
            Log.i(TAG, "send message(result=" + sendResult + "): " + message);
            return sendResult;
        } else {
            return false;
        }
    }

    /**
     * 关闭socket
     */
    public void shutDownSocket() {
        if (mWebSocket != null) {
            mWebSocket.close(1000, "disconnect");
            mWebSocket = null;
        }
    }

}
