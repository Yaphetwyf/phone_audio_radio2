package com.example.yaphet.phone_audio_radio1.util;

import android.app.Application;
import android.content.Context;

/**
 * 获得全局context
 * Created by WYF on 2017/11/7.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
