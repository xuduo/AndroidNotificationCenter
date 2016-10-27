AndroidNotificationCenter
========
[中文介绍](README-CN.MD)

Same as EventBus , Except for

 * Pros
    * Only 150 lines of code.
    * register/unregister call 100 times faster than EventBus!(when adding 5000 or more observers)
    * post call, about 10 times faster than EventBus.
    * No Annotation! Event is Interface based, using java.lang.reflect.Proxy, so compile time check for typo.
    
 * Cons
    * no sticky feature
    * event can only be received in main thread

## NotificationCenter in 3 steps

1.Define events as java Interface, sub

```
public interface SomeEvent {

    void someMethodName(Message param1);

}
```

Sub interface is also Ok, for organising events
```
public interface SomeEventCategory {

    interface Event1{
        void method1();
        void method2();
    }
    
    interface Event2{
        void method1();
        void method2();
    }

}
```

2.Prepare observers: Register your observer (in your onCreate or in a constructor):

```
NotificationCenter.INSTANCE.addObserver(this);
```

Implement your Event Interface
```
class SomeActivity implements SomeEvent {

    void someMethodName(Message param1);
    
}
```

3.Post events:
```
NotificationCenter.INSTANCE.getObserver(SomeEvent.class).someMethodName(new Message());
```

## Add NotificationCenter to your project

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.yy/notification-center-android/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.yy/notification-center-android)

maven
```xml
<dependency>
    <groupId>com.yy</groupId>
    <artifactId>notification-center-android</artifactId>
    <version>${version}</version>
</dependency>
```


gradle
```groovy
compile 'com.yy:android-push-sdk:${versoion}'
```
