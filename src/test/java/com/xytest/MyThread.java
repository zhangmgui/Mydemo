package com.xytest;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by zhangmg on 2017/4/21.
 */
public class MyThread {
    public  ThreadLocal<String> tlocal = new ThreadLocal<>();
    @Test
    public  void doTest(){
        ArrayList<String> strArr = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    strArr.add("线程1");
                    System.exit(1);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    strArr.add("线程2");
                }
            }
        }).start();
        strArr.forEach(o -> System.out.println(o));
    }
}

