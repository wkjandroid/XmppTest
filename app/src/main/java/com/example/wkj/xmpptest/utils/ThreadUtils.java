package com.example.wkj.xmpptest.utils;

import android.os.Handler;

/**
 * Created by wkj on 2017/3/20.
 *
 */

public class ThreadUtils {
    //子线程执行task
    public static void runInThread(Runnable task){
        new Thread(task).start();
    }
    //主线程里的一个handler
    public static Handler mHandler=new Handler();
    //ui线程执行task
    public static void runInUiThread(Runnable task){
        mHandler.post(task);

    }
}
