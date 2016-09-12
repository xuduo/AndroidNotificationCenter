package com.yy.androidlib.util.notification;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOS NSNotificationCenter for Android
 */
public enum NotificationCenter {
    INSTANCE;
    public static final String TAG = "notification";
    private Map<Class<?>, Proxy> proxyMap;
    private long mainThreadId;
    Handler handler;
    Map<Object, Boolean> observers;

    NotificationCenter() {
        proxyMap = new HashMap<>();
        observers = new ConcurrentHashMap<>();
        Looper mainLooper = Looper.getMainLooper();
        handler = new Handler(mainLooper);
        mainThreadId = mainLooper.getThread().getId();
    }

    /**
     * add observer by annotation
     *
     * @param observer
     */
    public void addObserver(final Object observer) {
        if (isMainThread()) {
            doAddObserver(observer);
        } else {
            Log.w(TAG, String.format("trying to add observer in non main thread: " + observer.getClass()));
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
        observers.put(observer, true);
    }

    public void removeObserver(final Object observer) {
        if (isMainThread()) {
            doRemoveObserver(observer);
        } else {
            Log.w(TAG, String.format("trying to remove observer in non main thread: " + observer.getClass()));
            removeObserverLater(observer);
        }
    }

    /**
     * observer will be removed later
     *
     * @param observer
     */
    private void removeObserverLater(final Object observer) {
        handler.post(new Runnable() {
            public void run() {
                doRemoveObserver(observer);
            }
        });
    }

    private void doRemoveObserver(Object observer) {
        observers.remove(observer);
    }

    public void addStickyObserver(final Object observer) {
        addObserver(observer);
        handler.post(new Runnable() {
            public void run() {
                getStickyMessage(observer);
            }
        });
    }

    private void getStickyMessage(Object observer) {
        for (Class callback : proxyMap.keySet()) {
            Proxy proxy = getProxy(callback);
            proxy.getStickyNotification().sendStickyMessage(observer);
        }
    }

    /**
     * @param callback
     * @return not null
     */
    private <T> Proxy<T> getProxy(Class<T> callback) {
        Proxy<T> proxy = proxyMap.get(callback);
        if (proxy == null) {
            proxy = addProxy(callback);
        }
        return proxy;
    }

    private <T> Proxy<T> addProxy(Class<T> callback) {
        Proxy<T> proxy = proxyMap.get(callback);
        if (proxy == null) {
            proxy = new Proxy<T>(callback);
            proxyMap.put(callback, proxy);
        }
        return proxy;
    }

    public <T> T getObserver(Class<T> callback) {
        return getProxy(callback).getNotification().getObserver();
    }

    public <T> T getStickyObserver(Class<T> callback) {
        return getProxy(callback).getStickyNotification().getObserver();
    }

    public void removeAll() {
        observers.clear();
    }

    @Deprecated
    /**
     * @deprecated no need to add callback before invoking since release 1.0.31
     */
    public void addCallbacks(Class callbackParent) {
        // do nothing
    }
}
