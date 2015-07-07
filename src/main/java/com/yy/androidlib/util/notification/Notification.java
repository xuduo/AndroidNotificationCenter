package com.yy.androidlib.util.notification;

import android.os.Handler;
import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Notification<T> implements InvocationHandler {
    private final Set<Object> observers;
    private Handler mainHandler;
    private T observerProxy = null;
    private Class<T> callback;

    public Notification(Class<T> callback, Handler handler, Set<Object> observers) {
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
        List<Object> threadSafe = new ArrayList<Object>(observers);
        for (Object observer : threadSafe) {
            if (callback.isInstance(observer)) {
                try {
                    method.invoke(observer, args);
                } catch (Exception e) {
                    Log.e("NotificationCenter", "invoke error, method: " + method.getName(), e);
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
