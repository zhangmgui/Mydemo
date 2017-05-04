package com.xytest.config;

import org.aspectj.lang.annotation.Aspect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangmg on 2017/5/4.
 */
@Aspect
public class transactionAop {
    private static ExecutorService executorService = Executors.newWorkStealingPool();
    public void Atest(){
        Object[] a = new Object[3];
        int b = (int)a[1];
    }
}
