package com.merrichat.net.rxjava;

import io.reactivex.observers.DefaultObserver;

/**
 * Created by wangweiwei on 17-12-26.
 */

public class BaseSubscribe<T> extends DefaultObserver<T> {

    @Override
    public void onError(Throwable e) {
        onDataError(e);
    }

    @Override
    public void onComplete() {
        onDataComplete();
    }
    @Override
    public void onNext(T t) {
        onDataSuccess(t);
    }

    public void onDataSuccess(T t) {

    }

    public void onDataError(Throwable e) {
    }

    public void onDataComplete() {

    }
}
