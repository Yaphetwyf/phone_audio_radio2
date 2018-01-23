package com.example.yaphet.phone_audio_radio1.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.activity.SystemAudioActivity;
import com.example.yaphet.phone_audio_radio1.activity.SystemVatamioActivity;
import com.example.yaphet.phone_audio_radio1.domin.RadioInfo;
import com.example.yaphet.phone_audio_radio1.util.MyApplication;
import com.example.yaphet.phone_audio_radio1.util.SavaWordNet;

import java.io.IOException;
import java.util.ArrayList;

/**
 * MusicPlayerService音乐的服务类
 */
public class MusicPlayerService extends Service {

    public static final String OPENAUDIO = "com.atguigu.mobileplayer_OPENAUDIO";
    private ArrayList<RadioInfo> radioInfos;
    private int musicPosition=0;
    /**
     * 播放音乐
     */
    private MediaPlayer mediaPlayer;
    /**
     * 当前播放的音频文件对象
     */
    private RadioInfo radioInfo;
    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 1;
    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGLE = 2;
    /**
     * 全部循环
     */
    public static final int REPEAT_ALL = 3;

    /**
     * 播放模式
     */
    private static int playmode = REPEAT_NORMAL;

    private MusicPlayerBinder playerBinder = new MusicPlayerBinder();
    private Notification notification;

    public class MusicPlayerBinder extends Binder {

        public void openAudio(int position) {
            musicPosition=position;
            openMusicAudio(position);
        }
        public void start() {
            mediaPlayer.start();
            //服务在的时候状态栏显示
            notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent=new Intent(MyApplication.getContext(), SystemAudioActivity.class);
            intent.putExtra("notification",true);
            PendingIntent pi=PendingIntent.getActivity(MyApplication.getContext(),0,intent,0);
            notification=new NotificationCompat.Builder(MyApplication.getContext())
                    .setContentTitle("321音乐")
                    .setContentText("正在播放"+radioInfo.getDisplayName())
                    .setSmallIcon(R.drawable.login_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.notification_music_playing))
                    .setContentIntent(pi)
                    .build();
            notificationManager.notify(1,notification);
        }

        public void pause() {
            mediaPlayer.pause();
        }

        public void stop() {

        }

        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        public String getArtist() {
            return radioInfo.getArtist();
        }

        public String getName() {

            return radioInfo.getDisplayName();
        }

        public void setPlayMode(int playmode) {
            MusicPlayerService.playmode=playmode;
            SavaWordNet.putMusicMode(MyApplication.getContext(),"musicmodle",playmode);//在文件中保存数据
            if(playmode==MusicPlayerService.REPEAT_SINGLE){
                //单曲循环播放-不会触发播放完成的回调
                mediaPlayer.setLooping(true);
            }else{
                //不循环播放
                mediaPlayer.setLooping(false);
            }
        }
        public int getPlayMode() {
            return playmode;
        }

        public void pre() {
            openPreMusic();

        }

        public void next() {
            //1.根据模式，获得下一个的位置
            //2.根据当前的模式和下标位置打开音频
            openNextMusic();
        }

        private void openNextMusic() {
            int playMode = getPlayMode();//读取当前的播放模式
            if(playMode== MusicPlayerService.REPEAT_NORMAL) {
                //当顺序播放的时候，点击下一个，就跳转到下一个，当到达最后一个时就停止
                musicPosition++;
                if(musicPosition<radioInfos.size()) {
                    openAudio(musicPosition);
                }


            }else if(playMode==MusicPlayerService.REPEAT_SINGLE) {
                //当设置为单曲循环时，点击下一首跳转到下一首，当点击到最后一个时，位置置为0，但是当前音乐播放完成时循环播放当前音乐
                musicPosition++;
                if(musicPosition>=radioInfos.size()) {
                    musicPosition=0;
                }
                openAudio(musicPosition);
            }else if(playMode==MusicPlayerService.REPEAT_ALL) {
                //当全部循环的时候，点击下一个，就跳转到下一个，当到达最后一个时就跳转到第一个
                musicPosition++;
                if(musicPosition>=radioInfos.size()) {
                    musicPosition=0;
                }
                openAudio(musicPosition);
            }
        }

        public String getAudioPath() {
            return radioInfo.getAdressAbso();
        }

        public Boolean isPlayering() {
            return mediaPlayer.isPlaying();
        }
        public void Seekto(int progress){
            if(mediaPlayer!=null) {
                mediaPlayer.seekTo(progress);
            }
        }

        public int getAudioSessionId() {
            return mediaPlayer.getAudioSessionId();
        }
    }
    private void openPreMusic() {
        int playMode = playerBinder.getPlayMode();//读取当前的播放模式
        if(playMode== MusicPlayerService.REPEAT_NORMAL) {
            //当顺序播放的时候，点击上一个，就跳转到上一个，当到达第一个时就停止
            musicPosition--;
            if(musicPosition>=0) {
                playerBinder.openAudio(musicPosition);
            }
        }else if(playMode==MusicPlayerService.REPEAT_SINGLE) {
            //当设置为单曲循环时，点击上一首跳转到上一首，当点击到第一个时，位置置为最后，但是当前音乐播放完成时循环播放当前音乐
            musicPosition--;
            if(musicPosition<0) {
                musicPosition=radioInfos.size()-1;
            }
            playerBinder.openAudio(musicPosition);
        }else if(playMode==MusicPlayerService.REPEAT_ALL) {
            //当全部循环的时候，点击上一个，就跳转到上一个，当到达第一个时就跳转到最后一个
            musicPosition--;
            if(musicPosition<0) {
                musicPosition=radioInfos.size()-1;
            }
            playerBinder.openAudio(musicPosition);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {

        return playerBinder;
    }
    private NotificationManager notificationManager;
    @Override
    public void onCreate() {
        super.onCreate();
        playmode= SavaWordNet.GetMusicMode(MyApplication.getContext(),"musicmodle");
        //加载音乐列表
        getDataFromLocal();

    }

    private void getDataFromLocal() {//服务默认在主线程中执行
        new Thread() {
            @Override
            public void run() {
                super.run();
                radioInfos = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//视频的文件大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST,//歌曲的演唱者
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        RadioInfo radioInfo = new RadioInfo();
                        radioInfos.add(radioInfo);
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        radioInfo.setArtist(artist);
                        String adressAbso = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        radioInfo.setAdressAbso(adressAbso);
                        String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        radioInfo.setDisplayName(displayName);
                        String radioSize = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));//视频的大小
                        radioInfo.setRadioSize(radioSize);
                        String time = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//视频的总时长
                        radioInfo.setTime(time);
                    }
                    cursor.close();
                }
            }
        }.start();
    }
    /**
     * 根据位置打开对应的音频文件,并且播放
     *
     * @param position
     */
    private void openMusicAudio(int position) {
        if (radioInfos != null && radioInfos.size() > 0) {
            radioInfo = radioInfos.get(position);
            if (mediaPlayer != null) {
                //mediaPlayer.release();
                mediaPlayer.reset();
            }
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(radioInfo.getAdressAbso());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.prepareAsync();//异步消息，处理好后回调onPrepared方法

                if(playmode==MusicPlayerService.REPEAT_SINGLE){
                    //单曲循环播放-不会触发播放完成的回调
                    mediaPlayer.setLooping(true);
                }else{
                    //不循环播放
                    mediaPlayer.setLooping(false);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MusicPlayerService.this, "音乐列表未准备好", Toast.LENGTH_SHORT).show();
            //有可能数据还未加载成功，因为在子线程中加载列表
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
           //播放出错时，播放下一个
            playerBinder.next();
            return true;
        }
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //当准备好的时候发一条广播，activity中显示名称和艺术家
            Intent intent=new Intent("WYF.BROADCAST_ACTIVITY_SHOW");
            sendBroadcast(intent);
            playerBinder.start();
        }
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //播放完成时，播放下一个
            playerBinder.next();
        }
    }
}

