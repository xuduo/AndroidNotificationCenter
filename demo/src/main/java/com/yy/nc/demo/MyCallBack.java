package com.yy.nc.demo;

/**
 * Created by huangzhilong on 2016/9/20.
 */
public interface MyCallBack {
    interface Test {
        void success(Message message);
    }

    interface Confusion {
        void failed(Message message);
    }

    interface SpeedTest {
        void costTime(String type, long time);
    }
}
