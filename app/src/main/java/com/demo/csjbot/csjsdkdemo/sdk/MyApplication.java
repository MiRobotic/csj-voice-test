package com.demo.csjbot.csjsdkdemo.sdk;

import android.app.Application;

import com.csjbot.coshandler.core.CsjRobot;

/**
 * Created by Administrator on 2019/7/13.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 初始化SDK
         */
        CsjRobot.getInstance().init(this);
    }
}
