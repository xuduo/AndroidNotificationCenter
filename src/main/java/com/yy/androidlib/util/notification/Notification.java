package com.yy.androidlib.util.notification;

import android.os.Handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import android.util.Log;

public class Notification<T> implements InvocationHandler {
    private final Map<Object, Boolean> observers;
    private Handler mainHandler;
    private T observerProxy = null;
    private Class<T> callback;

    public Notification(Class<T> callback, Handler handler, Map<Object, Boolean> observers) {
        this.callback = callback;
        this.mainHandler = handler;
        this.observers = observers;
    }

    public Object invoke(Object proxy, final Method method, final Object[] args) {
        mainHandler.post(new Runnable() {
            public void run() {
                doInvoke(method, args);
            }
        });
        return null;
    }

    private void doInvoke(Method method, Object[] args) {
        for (Object observer : observers.keySet()) {
            if (callback.isInstance(observer)) {
                try {
                    method.invoke(observer, args);
                } catch (Exception e) {
                    Log.e(NotificationCenter.TAG, "invoke error, method: " + method.getName(), e);
                }
            }
        }
    }

    public T getObserver() {
        if (observerProxy == null) {
            observerProxy = (T) Proxy.newProxyInstance(callback.getClassLoader(), new Class<?>[]{callback}, this);
        }
        return observerProxy;
    }
}
