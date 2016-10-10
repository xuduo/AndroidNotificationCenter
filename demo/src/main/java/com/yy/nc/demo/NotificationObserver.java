package com.yy.nc.demo;

import android.app.Activity;

/**
 * Created by huangzhilong on 2016/9/20.
 */
public class NotificationObserver extends Activity implements SomeEvent {

    @Override
    public void someMethodName(Message message) {
        MainActivity.indexs++;
        if (MainActivity.indexs == MainActivity.hitCount * MainActivity.postCount) {
            MainActivity.instance.done("notification");
        }
    }

}
