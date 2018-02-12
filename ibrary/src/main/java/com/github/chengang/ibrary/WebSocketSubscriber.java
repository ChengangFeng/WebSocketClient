package com.github.chengang.ibrary;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 封装的Subscriber
 *
 * @author 陈岗不行陈
 * @date 2017/8/6
 */

public abstract class WebSocketSubscriber<String> extends ResourceSubscriber<String> {

    @Override
    public void onNext(String message) {
        onMessage(message);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {

    }

    public abstract void onMessage(String message);
}
