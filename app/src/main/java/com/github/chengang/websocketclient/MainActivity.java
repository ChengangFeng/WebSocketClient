package com.github.chengang.websocketclient;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.chengang.ibrary.WebSocketClient;
import com.github.chengang.ibrary.WebSocketSubscriber;

import okhttp3.OkHttpClient;

/**
 * websocket测试连接demo
 *
 * @author FengChengang
 * @date 2018/2/12
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkHttpClient okHttpClient = new OkHttpClient();
        WebSocketClient.getConnectionProperty()
                //webSocket 地址
                .setWsUrl("ws://echo.websocket.org")
                //okHttpClient
                .setOkHttpClient(okHttpClient)
                //没收到消息的超时时间，超过这个时间，websocket自动重连
                .setTimeout(3 * 60);
    }

    /**
     * 打开websocket
     * @param view view
     */
    public void openWebSocket(View view) {
        WebSocketClient.getInstance().openSocketService().subscribeWith(
                new WebSocketSubscriber<String>() {
                    @Override
                    public void onMessage(String message) {
                        Log.i(TAG, message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.e(TAG, e.toString());
                    }
                }
        );
    }

    /**
     * 关闭websocket
     * @param view
     */
    public void closeWebSocket(View view) {
        WebSocketClient.getInstance().shutDownSocket();
    }

    public void sendMessage(View view) {
        String message = "test" + SystemClock.currentThreadTimeMillis();
        WebSocketClient.getInstance().sendMessage(message);
    }


}
