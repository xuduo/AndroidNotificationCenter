package com.yy.nc.demo;

/**
 * Created by huangzhilong on 2016/9/21.
 */
public class Message {
    private long time;
    private String message;

    public Message(long time) {
        this.time = time;
        this.message = "test";
    }

    public long getTime() {
        return time;
    }
}
