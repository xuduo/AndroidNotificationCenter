package com.yy.androidlib.util.notification;

/**
 * Created by huangzhilong on 2016/9/12.
 */
public class Proxy<T> {
    private Notification<T> notification;
    private StickyNotification<T> stickyNotification;
    private Class<T> callback;

    public Proxy(Class<T> callback) {
        this.callback = callback;
    }

    public Notification<T> getNotification() {
        if (notification == null) {
            notification = new Notification<>(callback);
        }
        return notification;
    }

    public StickyNotification<T> getStickyNotification() {
        if(stickyNotification == null){
            stickyNotification = new StickyNotification<>(callback);
        }
        return stickyNotification;
    }
}
