package com.yy.androidlib.util.notification;

import android.util.Log;

import java.lang.reflect.*;

/**
 * Created by huangzhilong on 2016/9/12.
 */
public class Notification<T> implements InvocationHandler {
    public T observerProxy;
    public Class<T> callback;

    public Notification(Class<T> callback) {
        this.callback = callback;
    }

    @Override
    public Object invoke(Object o, final Method method, final Object[] args) throws Throwable {
        NotificationCenter.INSTANCE.handler.post(new Runnable() {
            @Override
            public void run() {
                doInvoke(method, args);
            }
        });
        return null;
    }

    private void doInvoke(Method method, Object[] args) {
        for (Object observer : NotificationCenter.INSTANCE.observers.keySet()) {
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
            observerProxy = (T) java.lang.reflect.Proxy.newProxyInstance(callback.getClassLoader(), new Class<?>[]{callback}, this);
        }
        return observerProxy;
    }

}
