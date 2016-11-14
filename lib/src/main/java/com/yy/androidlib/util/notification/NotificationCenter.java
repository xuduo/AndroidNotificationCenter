package com.yy.androidlib.util.notification;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOS NSNotificationCenter for Android
 */
public enum NotificationCenter {
    INSTANCE;

    public static final String TAG = "NotificationCenter";
    private Map<Class<?>, Notification> notificationMap;
    private Handler handler;
    private Map<Object, Boolean> allObserver;

    NotificationCenter() {
        notificationMap = new ConcurrentHashMap<>();
        handler = new Handler(Looper.getMainLooper());
        allObserver = new ConcurrentHashMap<>();
    }

    public synchronized void addObserver(final Object observer) {
        allObserver.put(observer, true);
        for (Class<?> i : getAllInterfaces(observer.getClass())) {
            Notification notification = notificationMap.get(i);
            if (notification != null) {
                notification.getObservers().put(observer, true);
            }
        }
    }

    public synchronized void removeObserver(final Object observer) {
        allObserver.remove(observer);
        for (Class<?> i : getAllInterfaces(observer.getClass())) {
            Notification notification = notificationMap.get(i);
            if (notification != null) {
                notification.getObservers().remove(observer);
            }
        }
    }

    private <T> Notification<T> getNotification(Class<T> callback) {
        Notification notification = notificationMap.get(callback);
        if (notification == null) {
            notification = addNotification(callback);
        }
        return notification;
    }

    private synchronized  <T> Notification<T> addNotification(Class<T> callback) {
        Notification<T> notification = notificationMap.get(callback);
        if (notification == null) {
            notification = new Notification<>(callback, handler);
            notificationMap.put(callback, notification);
        }

        //addobserver
        for (Object observer : allObserver.keySet()) {
            if (callback.isInstance(observer)) {
                notification.getObservers().put(observer, true);
            }
        }
        return notification;
    }

    public <T> T getObserver(Class<T> callback) {
        return getNotification(callback).getObserver();
    }

    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<>(interfacesFound);
    }

    private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }
}