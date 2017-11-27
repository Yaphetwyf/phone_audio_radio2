package com.example.yaphet.phone_audio_radio1.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.example.yaphet.phone_audio_radio1.R;

public class SplashActivity extends Activity {
    private Intent intent;
    private Handler handler;
    private String Tag=SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                Log.e(Tag,name);
                //主线程执行
                StartActivity();
            }
        }, 3000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case  MotionEvent.ACTION_DOWN:
                handler.removeCallbacksAndMessages(null);
               StartActivity();
                break;
        }
        return true;
    }
    private void StartActivity() {
        intent=new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
