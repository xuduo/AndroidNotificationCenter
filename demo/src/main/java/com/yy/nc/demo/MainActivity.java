package com.yy.nc.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yy.androidlib.util.notification.NotificationCenter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements MyCallBack.SpeedTest {

    public static int testCount, testRate;
    public static long times, indexs, availabCount;

    private EditText editCount, editRate, editPost;
    private TextView tv_add, tv_post, tv_remove, tv_state;
    private final static int INIT_STATE = 0;
    private final static int TESTING_STATE = 1;
    private int state = INIT_STATE;
    private List<Object> observes;
    private int postCount;
    private long postNotificationTime, postEventBusTime;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editCount = (EditText) findViewById(R.id.et_count);
        editCount.setText("1000");
        editRate = (EditText) findViewById(R.id.et_rate);
        editRate.setText("10");
        editPost = (EditText) findViewById(R.id.et_post);
        editPost.setText("100");

        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_post = (TextView) findViewById(R.id.tv_post);
        tv_remove = (TextView) findViewById(R.id.tv_remove);
        tv_state = (TextView) findViewById(R.id.tv_state);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state != INIT_STATE) {
                    return;
                }
                String count = editCount.getText().toString();
                testCount = Integer.parseInt(count);
                String rate = editRate.getText().toString();
                testRate = Integer.parseInt(rate);
                String post = editPost.getText().toString();
                postCount = Integer.parseInt(post);
                if (testCount <= 0 || testRate <= 0 || postCount <= 0) {
                    Toast.makeText(MainActivity.this, "输入错误!", Toast.LENGTH_SHORT);
                }

                state = TESTING_STATE;
                if (observes == null || observes.size() == 0) {
                    observes = new ArrayList<>(testCount);
                    availabCount = 0;
                    int remainder = (testCount / (testCount * testRate / 100));
                    for (int i = 0; i < testCount; i++) {
                        if (i % remainder == 0) {
                            observes.add(new MyListener(MainActivity.this));
                            availabCount++;
                        } else {
                            observes.add(new ErrorListener());
                        }
                    }
                }

                ((TextView) findViewById(R.id.tv_number)).setText("availabCount: " + availabCount + "  " + " postCount:" + postCount);
                tv_state.setText("notification addObservering");
                long startTime = System.currentTimeMillis();
                for (int i = 0; i < observes.size(); i++) {
                    NotificationCenter.INSTANCE.addObserver(observes.get(i));
                }
                tv_add.setText("addObserver: notification  " + (System.currentTimeMillis() - startTime));

                tv_state.setText("EventBus addObservering");
                startTime = System.currentTimeMillis();
                for (int i = 0; i < observes.size(); i++) {
                    EventBus.getDefault().register(observes.get(i));
                }
                tv_add.setText(tv_add.getText().toString() + "   EventBus " + (System.currentTimeMillis() - startTime));

                times = 0;
                postEventBusTime = 0;
                postNotificationTime = 0;
                tv_state.setText("notification postMsging");
                testNotificationPost();
            }
        });
    }

    private void testNotificationPost() {
        times++;//对应postCount，表示postmsg的次数
        indexs = 0;
        NotificationCenter.INSTANCE.getObserver(MyCallBack.Test.class).success(new Message(System.currentTimeMillis()));
    }

    private void testEventBus() {
        times++;
        indexs = 0;
        EventBus.getDefault().post(new Message(System.currentTimeMillis()));
    }

    @Override
    public void costTime(String type, long time) {
        if (type.equals("notification")) {
            postNotificationTime = postNotificationTime + time;
            if (times < postCount) {
                testNotificationPost();
            } else {
                System.out.println("start post eventBus");
                tv_state.setText("EventBus postMsging");
                times = 0;
                testEventBus();
            }
        } else {
            postEventBusTime = postEventBusTime + time;
            if (times < postCount) {
                testEventBus();
            } else {
                tv_post.setText("postMsg: notification  " + postNotificationTime + "  eventBus  " + postEventBusTime);
                removeObserver();
            }
        }
    }

    private void removeObserver() {
        tv_state.setText("notification removeObservering");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < observes.size(); i++) {
            NotificationCenter.INSTANCE.removeObserver(observes.get(i));
        }
        tv_remove.setText("removeObserver: notification  " + (System.currentTimeMillis() - startTime));

        tv_state.setText("EventBus removeObservering");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < observes.size(); i++) {
            EventBus.getDefault().unregister(observes.get(i));
        }
        tv_remove.setText(tv_remove.getText().toString() + "  EventBus  " + (System.currentTimeMillis() - startTime));
        observes.clear();
        state = INIT_STATE;
        tv_state.setText("Test end");
    }
}
