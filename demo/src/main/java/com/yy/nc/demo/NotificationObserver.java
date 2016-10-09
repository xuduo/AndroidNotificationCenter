package com.yy.nc.demo;

import android.app.Activity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huangzhilong on 2016/9/20.
 */
public class NotificationObserver extends Activity implements MyCallBack {

    @Override
    public void success(Message message) {
        MainActivity.indexs++;
        if (MainActivity.indexs == MainActivity.hitCount * MainActivity.postCount) {
            MainActivity.instance.done("notification");
        }
    }

}
