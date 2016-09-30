package com.yy.nc.demo;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by huangzhilong on 2016/9/21.
 */
public class ErrorListener implements MyCallBack.Confusion{

    @Subscribe
    public void onEvent(int index) {
        System.out.println("ErrorListener: " + index);
    }

    @Override
    public void failed(Message message) {
        System.out.println("ErrorListener: " + message.getTime());
    }
}
