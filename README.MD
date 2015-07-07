##maven dependency

###添加maven依赖
```
        <dependency>
            <groupId>com.yy</groupId>
            <artifactId>util</artifactId>
            <version>1.0.0</version>
        </dependency>
```


### 1. NotificationCenter

IOS NotificationCenter android implementation

```
//callback interface
public interface SampleLoginCallback {

    interface Login {
        void onLogin(String userName);
        void onLogout();
    }
}


public class NotificationActivity extends BaseFragmentActivity implements SampleLoginCallback.Login{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.INSTANCE.addObserver(this);
        setContentView(R.layout.activity_notification);
        findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyLogin();
            }
        });
        findViewById(R.id.btn_logout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyLogout();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.INSTANCE.removeObserver(this);
    }

    private void notifyLogin() {
        NotificationCenter.INSTANCE.getObserver(SampleLoginCallback.Login.class).onLogin("Sample");
    }

    private void notifyLogout() {
        NotificationCenter.INSTANCE.getObserver(SampleLoginCallback.Login.class).onLogout();
    }

    @Override
    public void onLogin(String userName) {
        Toast.makeText(this, String.format("onLogin, userName: %s", userName), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLogout() {
        Toast.makeText(this, "onLogout", Toast.LENGTH_SHORT).show();
    }
}


```

### 2. http

base on LoopJ

```
AsyncHttp.get(urlEditText.getText().toString(), new ResultCallback() {

                    @Override
                    public void onSuccess(String url, int statusCode, String result) {
                        resultTextView.setText(result);
                    }

                    @Override
                    public void onFailure(String url, int statusCode, int errorType, Throwable throwable) {
                        resultTextView.setText("error");
                    }
                });
```

### 3.NetworkMonitor

```
public class NetworkMonitorActivity extends BaseFragmentActivity {

    private TextView resultTextView;
    private EditText urlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_monitor);
        resultTextView = (TextView) findViewById(R.id.tv_network_status);
        NetworkMonitor.addMonitor(this, new NetworkMonitor.NetworkChanged() {
            @Override
            public void onConnect() {
                resultTextView.setText("network is connected");
            }

            @Override
            public void onDisconnect() {
                resultTextView.setText("network is disconnected");
            }
        });
    }
}

```


###4.LibraryLoader 

.so file loader prevent Crash after app update

```
LibraryLoader.load(context,"libABC");
```

###4.Logger

logger api like Log4j 
write to file (auto rolling)


```
public class LoggingActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tips = new TextView(this);
        tips.setText(String.format("Check the logs in Logcat and log file in directory '%s'", getLogDir()));
        addContentView(tips, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        initLogging(false);
        outputTestLogs();
    }

    private void initLogging(boolean isDebuggable) {
        YYAppender.LogOptions options = new YYAppender.LogOptions();
        options.logFileName = "log.txt";
        options.logLevel = isDebuggable ? LogOptions.LEVEL_INFO : LogOptions.LEVEL_VERBOSE;
        YYAppender logWriter = new YYAppender(getLogDir(), options);
        logWriter.setUniformTag("MyLogTag");
        Logger.init(logWriter);
    }

    /**
     * Logs will not be output if it is lower than the {@link LogOptions#logLevel}
     * You can change the {@link LogOptions#logLevel} in {@link #initLogging(boolean)}
     * to write the low level logs
     */
    private void outputTestLogs() {
        Logger.verbose(this, "This is a verbose log");
        Logger.debug(this, "This is a debug log");
        Logger.info(this, "This is a info log");
        Logger.warn(this, "This is a warn log");
        Logger.error(this, "This is a error log");
        Logger.error(this, "This is a error log with parameters, int: %d, hex: %x, String: %s",
                10086, 0xFFFF, "test string");
        try {
            String testExceptionLog = null;
            testExceptionLog.hashCode();
        } catch (Exception e) {
            Logger.error(this, "some error text %s", "test", e);
        }

    }

    private String getLogDir() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator
                + "MyAppFolder" + File.separator + "log";
    }
}

```

