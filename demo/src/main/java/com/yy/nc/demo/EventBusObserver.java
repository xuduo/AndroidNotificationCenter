package com.yy.nc.demo;

import android.app.Activity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huangzhilong on 2016/9/20.
 */
public class EventBusObserver extends Activity {

    @Subscribe
    public void onEvent(Message message) {
        MainActivity.indexs++;
        if (MainActivity.indexs == MainActivity.hitCount * MainActivity.postCount) {
            MainActivity.instance.done("eventBus");
        }
    }
}
