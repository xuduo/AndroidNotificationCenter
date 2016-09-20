package com.yy.nc.demo;

import android.app.Activity;
import com.yy.nc.demo.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {
    public static final String TAG = "MainActivityTest";
    private String dnsService = "https://iplist.yy.com/ipList";
    private String protocol = "http://";
    private String api = "/speedTest";
    private int port = 80;
    private EditText editText;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.et_host);
        editText.setText("wstest.yy.com");
        textView = (TextView) findViewById(R.id.tv_result);
        findViewById(R.id.btn_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

}
