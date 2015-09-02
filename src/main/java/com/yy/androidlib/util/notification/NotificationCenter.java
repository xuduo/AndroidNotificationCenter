package com.yy.androidlib.util.notification;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOS NSNotificationCenter for Android
 */
public enum NotificationCenter {
    INSTANCE;

    private static final String TAG = "notification";
    private Map<Class<?>, Notification> notificationMap;
    private long mainThreadId;
    private Handler handler;
    private Set<Object> observers;

    NotificationCenter() {
        notificationMap = new ConcurrentHashMap<>();
        observers = new HashSet<Object>();
        Looper mainLooper = Looper.getMainLooper();
        handler = new Handler(mainLooper);
        mainThreadId = mainLooper.getThread().getId();
    }

    /**
     * add observer
     *
     * @param observer
     */
    public void addObserver(final Object observer) {
        if (isMainThread()) {
            doAddObserver(observer);
        } else {
            Log.w(TAG, String.format("trying to add observer in non main thread: %s", observer.getClass()));
            handler.post(new Runnable() {
                public void run() {
                    doAddObserver(observer);
                }
            });
        }
    }

    private boolean isMainThread() {
        return Thread.currentThread().getId() == mainThreadId;
    }

    private void doAddObserver(Object observer) {
        observers.add(observer);
    }

    public void removeObserver(final Object observer) {
        doRemoveObserver(observer);
    }

    private void doRemoveObserver(Object observer) {
        observers.remove(observer);
    }

    /**
     * @param callback
     * @return not null
     */
    private <T> Notification<T> getNotification(Class<T> callback) {
        Notification notification = notificationMap.get(callback);
        if (notification == null) {
            notification = addNotification(callback);
        }
        return notification;
    }

    private <T> Notification<T> addNotification(Class<T> callback) {
        Notification<T> notification = notificationMap.get(callback);
        if (notification == null) {
            notification = new Notification<T>(callback, handler, observers);
            notificationMap.put(callback, notification);
        }
        return notification;
    }

    public <T> T getObserver(Class<T> callback) {
        return getNotification(callback).getObserver();
    }

    public void removeAll() {
        observers.clear();
    }
}
