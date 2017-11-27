package com.example.yaphet.phone_audio_radio1.activity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.yaphet.phone_audio_radio1.util.MyApplication;

/**
 * Created by WYF on 2017/11/7.
 */

public class SecondFragment extends BaseFrament {
    private TextView textView;
    @Override
    protected View initView() {
        textView=new TextView(MyApplication.getContext());
        Log.e("TAG", "SecondFragment被初始化了");
        return textView;
    }

    @Override
    protected void initdata() {
        super.initdata();
        textView.setText("本地音乐");
    }
}
