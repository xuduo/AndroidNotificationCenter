package com.yy.nc.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yy.androidlib.util.notification.NotificationCenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    public static int testCount, testRate;
    public static int indexs, hitCount;
    public static MainActivity instance;

    private EditText editCount, editRate, editPost;
    private TextView tv_add, tv_post, tv_remove, tv_state;
    private List<Object> notificationCenterObserves;
    private List<Object> eventBusObservers;
    private List<Object> dummyObservers;
    public static int postCount;
    private long postNotificationTime, postEventBusTime;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        editCount = (EditText) findViewById(R.id.et_count);
        editCount.setText("5000");
        editRate = (EditText) findViewById(R.id.et_rate);
        editRate.setText("10");
        editPost = (EditText) findViewById(R.id.et_post);
        editPost.setText("2000");

        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_post = (TextView) findViewById(R.id.tv_post);
        tv_remove = (TextView) findViewById(R.id.tv_remove);
        tv_state = (TextView) findViewById(R.id.tv_state);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_test).setEnabled(false);
                String count = editCount.getText().toString();
                testCount = Integer.parseInt(count);
                String rate = editRate.getText().toString();
                testRate = Integer.parseInt(rate);
                String post = editPost.getText().toString();
                postCount = Integer.parseInt(post);
                if (testCount <= 0 || testRate <= 0 || postCount <= 0) {
                    Toast.makeText(MainActivity.this, "输入错误!", Toast.LENGTH_SHORT).show();
                    return;
                }
                hitCount = testCount * testRate / 100;
                notificationCenterObserves = new ArrayList<Object>(hitCount);
                eventBusObservers = new ArrayList<Object>(hitCount);
                dummyObservers = new ArrayList<Object>();
                for (int i = 0; i < hitCount; i++) {
                    eventBusObservers.add(new EventBusObserver());
                    notificationCenterObserves.add(new NotificationObserver());
                }

                for (int i = 0; i < testCount - hitCount; i++) {
                    dummyObservers.add(new MainActivity());
                }


                ((TextView) findViewById(R.id.tv_number)).setText("availabCount: " + hitCount + "  " + " postCount:" + postCount);
                tv_state.setText("NC add Observer");
                addNCObservers();
                addEventBusObservers();

                postEventBusTime = 0;
                postNotificationTime = 0;
                tv_state.setText("notification postMsging");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        testNotificationPost();
                    }
                });
            }
        });
    }

    private void addEventBusObservers() {
        long startTime;
        tv_state.setText("EventBus addObservering");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < notificationCenterObserves.size(); i++) {
            EventBus.getDefault().register(eventBusObservers.get(i));
        }
        for (Object o : dummyObservers) {
            EventBus.getDefault().register(o);
        }
        tv_add.setText(tv_add.getText().toString() + "   EventBus " + (System.currentTimeMillis() - startTime));

    }

    private void addNCObservers() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < notificationCenterObserves.size(); i++) {
            NotificationCenter.INSTANCE.addObserver(notificationCenterObserves.get(i));
        }
        for (Object o : dummyObservers) {
            NotificationCenter.INSTANCE.addObserver(o);
        }
        tv_add.setText("addObserver: NC  " + (System.currentTimeMillis() - startTime));
    }

    private void testNotificationPost() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                postNotificationTime = System.currentTimeMillis();
                for (int i = 0; i < postCount; i++) {
                    NotificationCenter.INSTANCE.getObserver(SomeEvent.class).someMethodName(new Message(System.currentTimeMillis()));
                }
            }
        });
    }

    private void testEventBus() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                postEventBusTime = System.currentTimeMillis();
                for (int i = 0; i < postCount; i++) {
                    EventBus.getDefault().post(new Message(System.currentTimeMillis()));
                }
            }
        });
    }

    public void done(String type) {
        indexs = 0;
        if (type.equals("notification")) {
            postNotificationTime = System.currentTimeMillis() - postNotificationTime;
            tv_state.setText("EventBus postMsging");
            testEventBus();
            tv_post.setText("postMsg: NC  " + postNotificationTime + "  eventBus  " + postEventBusTime);
        } else {
            postEventBusTime = System.currentTimeMillis() - postEventBusTime;
            tv_post.setText("postMsg: NC  " + postNotificationTime + "  eventBus  " + postEventBusTime);
            removeObserver();
        }
    }

    private void removeObserver() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                tv_state.setText("notification removeObservering");
                long startTime = System.currentTimeMillis();
                for (int i = 0; i < notificationCenterObserves.size(); i++) {
                    NotificationCenter.INSTANCE.removeObserver(notificationCenterObserves.get(i));
                }
                for (Object o : dummyObservers) {
                    NotificationCenter.INSTANCE.removeObserver(o);
                }
                tv_remove.setText("removeObserver: NC  " + (System.currentTimeMillis() - startTime));

                tv_state.setText("EventBus removeObservering");
                startTime = System.currentTimeMillis();
                for (int i = 0; i < eventBusObservers.size(); i++) {
                    EventBus.getDefault().unregister(eventBusObservers.get(i));
                }
                for (Object o : dummyObservers) {
                    EventBus.getDefault().unregister(o);
                }
                tv_remove.setText(tv_remove.getText().toString() + "  EventBus  " + (System.currentTimeMillis() - startTime));
                notificationCenterObserves.clear();
                tv_state.setText("Test end");
                ((Button) findViewById(R.id.btn_test)).setEnabled(true);
            }
        });

    }

    @Subscribe
    public void dummyMethod(String www) {

    }
}
