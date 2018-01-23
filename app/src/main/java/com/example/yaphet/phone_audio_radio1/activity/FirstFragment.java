package com.example.yaphet.phone_audio_radio1.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.adapter.RadioAdapter;
import com.example.yaphet.phone_audio_radio1.domin.RadioInfo;
import com.example.yaphet.phone_audio_radio1.util.LogUtil;
import com.example.yaphet.phone_audio_radio1.util.MyApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WYF on 2017/11/7.
 */

public class FirstFragment extends BaseFrament {
    private ListView lv_first_rudio;
    private TextView tv_text;
    private ProgressBar PB_press_center;
    private List<RadioInfo> radioInfos;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(radioInfos!=null&&radioInfos.size()>0) {

                //ProgressBar消失
                //显示数据
               lv_first_rudio.setAdapter(new RadioAdapter(radioInfos,true));
                //让进度条消失
                PB_press_center.setVisibility(View.GONE);
                lv_first_rudio.setOnItemClickListener(new MyListViewAdapter());
            }else{
                //显示没有数据
                tv_text.setVisibility(View.VISIBLE);
                tv_text.setText("没有视频文件"+radioInfos.toString());
                PB_press_center.setVisibility(View.GONE);
            }
        }
    };
    @Override
    protected View initView() {
        View view = View.inflate(MyApplication.getContext(), R.layout.listmenu, null);
        lv_first_rudio = (ListView) view.findViewById(R.id.lv_first_rudio);
        tv_text = (TextView) view.findViewById(R.id.tv_text);
        PB_press_center = (ProgressBar) view.findViewById(R.id.PB_press_center);

        Log.e("TAG", "FirstFragment被初始化了");
        return view;
    }
    @Override
    protected void initdata() {
        super.initdata();
        //初始化视频列表
        //在子线程中读取视频列表
        if(ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            
        }else {
            GetRadioList();
        }
    }
    private void GetRadioList() {
        radioInfos=new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;//注意
                Cursor cursor = MyApplication.getContext().getContentResolver().query(uri, null, null, null, null);
                if(cursor!=null) {
                    while( cursor.moveToNext()) {
                        RadioInfo radioInfo = new RadioInfo();
                        radioInfos.add(radioInfo);
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST));
                        radioInfo.setArtist(artist);
                        String adressAbso = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        radioInfo.setAdressAbso(adressAbso);
                        String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        radioInfo.setDisplayName(displayName);
                        String radioSize = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));//视频的大小
                        radioInfo.setRadioSize(radioSize);
                        String time = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));//视频的总时长
                        radioInfo.setTime(time);

                        handler.sendEmptyMessage(1);
                        //封装数据
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(1);
            }
            
        }).start();
}
    private class MyListViewAdapter implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            RadioInfo radioInfo = radioInfos.get(position);
           // Toast.makeText(MyApplication.getContext(), radioInfo.getAdressAbso(), Toast.LENGTH_SHORT).show();

          /*  //1.调起系统所有的播放器，Intent.ACTION_VIEW(用隐士意图)
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse(radioInfo.getAdressAbso()),"video*//*");
            MyApplication.getContext().startActivity(intent);*/

            //2.调起系统api所组成的播放器

            //显示意图调用系统API写的播放器,这种情况传递的是单一的地址
          /*  Intent intentmyRadio=new Intent(MyApplication.getContext(),SystemRadioActivity.class);
            intentmyRadio.setDataAndType(Uri.parse(radioInfo.getAdressAbso()),"video*//*");
            MyApplication.getContext().startActivity(intentmyRadio);*/
            //传递视频列表
            Intent intent=new Intent(MyApplication.getContext(),SystemRadioActivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("vadio_list",(Serializable)radioInfos);
            intent.putExtras(bundle);//传递视频列表
            intent.putExtra("position",position);
            MyApplication.getContext().startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case  1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    GetRadioList();
                }else{
                    Toast.makeText(MyApplication.getContext(), "你取消了授权", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
