package com.example.administrator.lv.coom;

import android.content.Context;

import com.lykj.aextreme.afinal.common.BaseApplication;

public class MyApplication extends BaseApplication {
    private static MyApplication app;

    public static MyApplication getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
