package com.yy.androidlib.util.notification;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by huangzhilong on 2016/9/12.
 */
public class StickyNotification<T> extends Notification<T> {
    private Map<Method, Object[]> stickyMsg;

    public StickyNotification(Class<T> callback) {
        super(callback);
        this.stickyMsg = new HashMap<>();
    }

    @Override
    public Object invoke(Object o, final Method method, final Object[] args) throws Throwable {
        super.invoke(o, method, args);
        NotificationCenter.INSTANCE.handler.post(new Runnable() {
            @Override
            public void run() {
                stickyMsg.put(method, args);
            }
        });
        return null;
    }

    public void sendStickyMessage(Object observer) {
        if (callback.isInstance(observer)) {
            for (Iterator<Method> it = stickyMsg.keySet().iterator(); it.hasNext(); ) {
                Method method = it.next();
                Object[] args = stickyMsg.get(method);
                try {
                    method.invoke(observer, args);
                    it.remove();
                } catch (Exception e) {
                    Log.e(NotificationCenter.TAG, "Sticky invoke error, method: " + method.getName(), e);
                }
            }
        }
    }
}
