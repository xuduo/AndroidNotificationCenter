EventBus
========
NotificationCenterAndroid is a ObserverPattern implementation for android, like NSNotificationCenter in ios.

 * uses java InvocationHandler 
    * static type interface ,prevent typo, if you change method name or argument type, observers won't compile without modification
    * no reflection used


##maven dependency

```
        <dependency>
            <groupId>com.yy</groupId>
            <artifactId>notification-center-android</artifactId>
            <version>1.0.1</version>
        </dependency>
```

##gradle dependency

```
        compile 'com.yy:notification-center-android:1.0.1'
```

### Usage

define interface (delegate in ios)
```
//callback interface
interface LoginCallback {
    void onLogin(String userName);
    void onLogout();
}

```

implement callback interface
add observer
```
public class NotificationActivity extends Activity implements LoginCallback {

 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        NotificationCenter.INSTANCE.addObserver(this); //add this activity to NotificationCenter
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        NotificationCenter.INSTANCE.addObserver(this); //add this activity to NotificationCenter
    }
    
    @Override
    protected void onDestroy() {
        NotificationCenter.INSTANCE.addObserver(this); //remove this activity to NotificationCenter
        super.onDestroy();
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

post Notification
```
 NotificationCenter.INSTANCE.getObserver(LoginCallback.class).onLogin("abc");
```

