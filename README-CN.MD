## Android NotificationCenter

大概4年前刚开始做Android的时候，我写的一个类似于EventBus的实现，目前应用于部门内大多数Android App。源码地址，文档 https://github.com/xuduo/AndroidNotificationCenter

- 无Annotation, 基于Java Proxy实现
- 某些场景下, 性能是EventBus的100倍以上

#### Java Proxy vs Annotation
NotificationCenter使用的核心技术是Java Proxy。 Android开发比较少会用到，在Java服务器开发中，经常会用到。

首先定义一个接口，如SomeEvent，并实现接口
```java
class SomeActivity implements SomeEvent {
  @不需要加注解标识， 只需要实现接口↑
  void someMethodName(String param1,int param2, Message param3);
}
```

post时候，传Interface进去，获取到该接口一个代理类
```java
    SomeEvent proxy = NotificationCenter.INSTANCE.getObserver(SomeEvent.class);
    proxy.someMethodName("a",1,new Message());
```
返回一个实现了SomeEvent接口的Proxy实例, 可以直接调用接口定义的方法.

好处？
- Android Studio里，可以使用Find Usages，Go To -> implementation(s)。
- 强类型，编译时检查。如果改了接口定义，实现如果不改，编译不过。
- 不会因为忘了加Annotation调不到。
- 方法参数可以是任何类型，如 int, String，EventBus必须是一个自定义的对象。
- 方法可以定义多参数， 如 onFail(int code, String message)。

####性能对比
测试手机:三星edge7 

EventBus版本: greenrobot最新 3.0.0

register 5000个对象， 其中20%的对象会收到post消息，起1个线程，循环post 100次，测试register，post，unregister的速度，单位毫秒

|          |   NotificationCenter  |   EventBus   |
|:--------:|:----------:|:-----------------------:|
| register   |   10 |                   1505|
| unregister |     8    |              956  |
| post |   246 |    3356 |

register/unregister性能**100**倍以上

post **10**倍以上

测试代码见项目demo

####为什么EventBus这么慢？

#####register
EventBus register中一段代码
```java
CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList<>();
            subscriptionsByEventType.put(eventType, subscriptions);
        } else {
            if (subscriptions.contains(newSubscription)) { // 对list使用contains
                throw new EventBusException("Subscriber " + subscriber.getClass() + " already registered to event " + eventType);
            }
        }
```
上面代码第6行，**对一个list进行contains调用**。这个list保存了某个@Subscription方法对应的所有observer，对于register n次，复杂度大概为O(n^{2})。

NotificationCenter的register，只是对map的put调用, 对于register n次，复杂度为O(n)。

#####unregister
EventBus unregister中一段代码
```java
            int size = subscriptions.size();
            for (int i = 0; i < size; i++) {// 每次remove 对list循环查找
                Subscription subscription = subscriptions.get(i);
                if (subscription.subscriber == subscriber) {
                    subscription.active = false;
                    subscriptions.remove(i);
                    i--;
                    size--;
                }
            }
```
类似于register ， 对list进行循环，判断是否==。对于unregister n次，复杂度大概为O(n^{2})。

NotificationCenter的register，只是对map的put调用, 对于unregister n次，复杂度为O(n)。

####post

主要因为EventBus用了一个PostPool，而且加了一个锁，并发越高，post越慢。

####综合对比
|          |  EventBus  |    NotificationCenter   |
|:--------:|:----------:|:-----------------------:|
| 实现方法 | Annotation | java.lang.reflect.Proxy |
| 代码量   |    50k jar |                  <150行 |
| 事件区分 |       POJO |               Interface |
| post线程 |    4种可选 |      主线程handler post |
| sticky   |       支持 |                  不支持 |

####为什么不支持指定post线程
观察者模式虽然可以解耦，但是过度使用，会使项目复杂度增加。

我们只在业务层到UI层使用NotificationCenter，因为UI有生命周期，业务层要做到不关心UI层的生命周期，只能使用观察者模式。UI层调用必须在主线程，所以NotificationCenter只默认支持主线程。

业务层内，如逻辑层，网络层，存储层之间通信，一般采用listener，delegate，callback方式，因为业务层的对象，大多是跟随APP的生命周期，一直存在。

####为什么不支持sticky功能
需要sticky的消息，业务层做存储比较合适，可以控制什么时候存，什么时候删，什么时候过期。依赖于EventBus会增加业务复杂度，可能出现难以预期的问题。
