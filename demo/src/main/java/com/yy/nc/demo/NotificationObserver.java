package com.yy.nc.demo;

import android.app.Activity;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huangzhilong on 2016/9/20.
 */
public class NotificationObserver extends Activity implements SomeEvent {

    @Override
    public void someMethodName(Message message) {
        MainActivity.indexs++;
        if (MainActivity.indexs % MainActivity.postCount == 0) {
            Log.d("NotificationObserver", "onEvent + " + MainActivity.indexs / MainActivity.hitCount);
        }
        if (MainActivity.indexs == MainActivity.hitCount * MainActivity.postCount) {
            MainActivity.instance.done("notification");
        }
    }

    public void onEvent(Message2 message) {

    }

    public void onEvent(Message3 message) {

    }

    public void onEvent(Message4 message) {

    }

    public void onEvent(Message5 message) {

    }

}
