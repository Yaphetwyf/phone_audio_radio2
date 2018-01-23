package com.example.yaphet.phone_audio_radio1.activity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.util.MyApplication;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {
    @Bind(R.id.tv_title_search)
    TextView textViewsearch;

    TextView tvTitleTextView;
    @Bind(R.id.Fl_main_centerFramelayout)
    FrameLayout FlMainCenterFramelayout;
    @Bind(R.id.RG_main_radiogroup)
    RadioGroup RGMainRadiogroup;
    private List<BaseFrament> framentLists = new ArrayList<>();
    private int position;
    private Fragment context;
    private long lasttime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initFragment();
        initLitener();
    }
    private void initLitener() {
        RGMainRadiogroup.setOnCheckedChangeListener(new MyRgListener());
        RGMainRadiogroup.check(R.id.RB_main_radiobutton1);
        textViewsearch.setOnClickListener(new MySearchListener());
    }
    private void initFragment() {
        framentLists.add(new FirstFragment());
        framentLists.add(new SecondFragment());
        framentLists.add(new ThirdFragment());
        framentLists.add(new FourFrament());
    }

    private class MySearchListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "语音搜索", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,Reach_voiceActivity.class));
        }
    }
    private class MyRgListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case  R.id.RB_main_radiobutton1:
                    position=0;
                    break;
                case  R.id.RB_main_radiobutton2:
                    position=1;
                    break;
                case  R.id.RB_main_radiobutton3:
                    position=2;
                    break;
                case  R.id.RB_main_radiobutton4:
                    position=3;
                    break;
            }
            Fragment fragment = getFragment(position);
            replaceFragment(context,fragment);
        }
    }
    private void replaceFragment(Fragment from,Fragment to) {
        if(from!=to) {
            context = to;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {//没有被添加，先隐藏当前的,然后添加
                if(from!=null) {
                    fragmentTransaction.hide(from);
                }
                if (to!=null){
                fragmentTransaction.add(R.id.Fl_main_centerFramelayout, to).commit();}
            }else {
                if(from!=null) {
                    fragmentTransaction.hide(from);
                }
                if(to!=null) {
                    fragmentTransaction.show(to).commit();
                }
            }
        }
    }
    private Fragment getFragment(int position) {

        return framentLists.get(position);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {//也可以用hander
            if((System.currentTimeMillis()-lasttime)>2000) {
                lasttime=System.currentTimeMillis();
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }else {
                //NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //notificationManager.cancelAll();
                //System.exit(0);//停掉进程所有
                return super.onKeyDown(keyCode, event);
            }
          return true;  
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
