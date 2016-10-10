package com.yy.nc.demo;

/**
 * Created by huangzhilong on 2016/9/20.
 */
public interface SomeEvent {

    void someMethodName(Message param1);

    interface Event1{
        void method1();
        void method2();
    }
}
