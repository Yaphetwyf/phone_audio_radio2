package com.example.yaphet.phone_audio_radio1.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by WYF on 2018/1/2.
 * 保存网络缓存文本的工具类
 *
 *
 */

public class SavaWordNet {
    public static void putSave(Context context,String key,String value){
     //保存数据
        SharedPreferences.Editor editor= context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
        Log.e("--------数据","数据保存成功");
    }

    public static String GetSave(Context context,String key){
        SharedPreferences data = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        String dataString = data.getString(key, "");
        return dataString;
    }


    /**
     * 扩展保存播放模式的状态
     */
    public static void putMusicMode(Context context,String key,int value){
        //保存模式
        SharedPreferences.Editor editor= context.getSharedPreferences("musicmode",Context.MODE_PRIVATE).edit();
        editor.putInt(key,value);
        editor.apply();
    }
    public static int GetMusicMode(Context context,String key){
        //获取模式
        SharedPreferences data = context.getSharedPreferences("musicmode", Context.MODE_PRIVATE);
        int dataInt = data.getInt(key, 0);
        return dataInt;
    }
}
