package com.example.yaphet.phone_audio_radio1.activity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.domin.Lyric;
import com.example.yaphet.phone_audio_radio1.service.MusicPlayerService;
import com.example.yaphet.phone_audio_radio1.util.LyricUtils;
import com.example.yaphet.phone_audio_radio1.util.MyApplication;
import com.example.yaphet.phone_audio_radio1.util.SavaWordNet;
import com.example.yaphet.phone_audio_radio1.util.Utils;
import com.example.yaphet.phone_audio_radio1.view.BaseVisualizerView;
import com.example.yaphet.phone_audio_radio1.view.ShowLyricView;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemAudioActivity extends Activity {
    boolean notification;
    private  int passedPotion=0;
    private MusicPlayerService.MusicPlayerBinder playerBinder;//得到操作方法的实例
    showViewReceiver viewReceiver;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;
    @Bind(R.id.tv_artist)
    TextView tvArtist;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.rl_top)
    RelativeLayout rlTop;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.seekbar_audio)
    SeekBar seekbarAudio;
    @Bind(R.id.btn_audio_playmode)
    Button btnAudioPlaymode;
    @Bind(R.id.btn_audio_pre)
    Button btnAudioPre;
    @Bind(R.id.btn_audio_start_pause)
    Button btnAudioStartPause;
    @Bind(R.id.btn_audio_next)
    Button btnAudioNext;
    @Bind(R.id.btn_lyrc)
    Button btnLyrc;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.showlyricview)
    ShowLyricView showLyricView;
    @Bind(R.id.baseVisualizerView)
    BaseVisualizerView baseVisualizerView;
    private final int  UPDATA_SEEKBAR_AUDIO=1;
    private final int UPDATA_LYRIC_AMONGTIME=2;
    private int parsetotalAudioIntduration;
    private Utils utils;
    ArrayList<Lyric> lyrics;
    Intent musicIntent;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case  UPDATA_SEEKBAR_AUDIO:
                    if(playerBinder.getName()!=null) {
                        tvName.setText(playerBinder.getName());
                    }
                    if(playerBinder.getArtist()!=null) {
                        tvArtist.setText(playerBinder.getArtist());
                    }

                    int currentPosition = playerBinder.getCurrentPosition();
                    seekbarAudio.setProgress(currentPosition);//设置Seekbar的进度
                    //设置时间进度的改变
                    String currentStringForTime = utils.stringForTime(currentPosition);
                    String TotalstringForTime = utils.stringForTime(parsetotalAudioIntduration);
                    tvTime.setText(currentStringForTime+"/"+TotalstringForTime);

                    handler.removeMessages(UPDATA_SEEKBAR_AUDIO);
                    handler.sendEmptyMessageDelayed(UPDATA_SEEKBAR_AUDIO, 1000);
                    break;
                case  UPDATA_LYRIC_AMONGTIME:
                    //得到当前的进度
                    float currentLyricPosition = playerBinder.getCurrentPosition();

                    //根据当前的时间，计算该高亮显示那一句
                    //1.把当前进度传入，更具当前位置计算显示下一句歌词
                    showLyricView.setShowLyric(currentLyricPosition);

                    //实时的发送消息
                    handler.removeMessages(UPDATA_LYRIC_AMONGTIME);//***必须加上，如果移除，则会一直处理此消息，系统会变慢
                    handler.sendEmptyMessage(UPDATA_LYRIC_AMONGTIME);
                    break;
            }
        }
    };
    ServiceConnection connection=new ServiceConnection() {
        /**
         * 当服务与活动绑定成功的时候调用
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playerBinder= (MusicPlayerService.MusicPlayerBinder) service;
            if(!notification) {//如果不是来自于状态栏，避免每次打开状态栏重新播放
                playerBinder.openAudio(passedPotion);//连接后开始播放
            }else{//来自于状态栏，需要重新设置视图变化
                Intent intent=new Intent("WYF.BROADCAST_ACTIVITY_SHOW");
                sendBroadcast(intent);
            }
        }
        /**
         * 当活动与服务断开连接的时候调用
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_audio);
        ButterKnife.bind(this);
        requestPermission();
        setDrawable();//设置帧动画
        getPosition();//获取位置
        bindandStartService();//绑定服务
        initViewData();

    }
private boolean isGriand;
    private void requestPermission() {
        if(ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SystemAudioActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},2);

        }else {
            isGriand=true;
        }

    }
    /**
     * 接收通知，显示view视图的初始化数据
     */
    private void initViewData() {
        utils=new Utils();

        seekbarAudio.setOnSeekBarChangeListener(new MyAudioSeekBarChangeListener());

        IntentFilter intentFilter=new IntentFilter();//通知可以修改成EventBus
        intentFilter.addAction("WYF.BROADCAST_ACTIVITY_SHOW");
        viewReceiver=new showViewReceiver();
        registerReceiver(viewReceiver,intentFilter);

    }
    class showViewReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            /*if(playerBinder.getName()!=null) {
                tvName.setText(playerBinder.getName());
            }
            tvArtist.setText(playerBinder.getArtist());*/
            //准备好的时候，更新seekbar
            int duration=0;
            if(playerBinder!=null) {
                duration = playerBinder.getDuration();//获得当前文件的总时长
            }
            parsetotalAudioIntduration = duration;
            seekbarAudio.setMax(parsetotalAudioIntduration);
            handler.sendEmptyMessage(UPDATA_SEEKBAR_AUDIO);

            checkMusicMode();

            //根据不同的歌词文件加载歌词
            showDifferentLyyric();

            //根据不同的音乐播放不同的频谱。Visualizer  6.0以上需要添加动态权限
            if(isGriand) {
                setupVisualizerFxAndUi();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVisualizer!=null) {
            mVisualizer.release();
        }
    }

    private Visualizer mVisualizer;
    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi()
    {

        int audioSessionid = playerBinder.getAudioSessionId();
        System.out.println("audioSessionid=="+audioSessionid);
        mVisualizer = new Visualizer(audioSessionid);
        // 参数内必须是2的位数
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 设置允许波形表示，并且捕获它
        baseVisualizerView.setVisualizer(mVisualizer);
        mVisualizer.setEnabled(true);
    }
    private void showDifferentLyyric() {
        LyricUtils lyricUtils=new LyricUtils();
        //得到当前播放歌曲的路径
        if(playerBinder!=null) {
            String audioPath = playerBinder.getAudioPath();
            int lastIndexOf = audioPath.lastIndexOf(".");
            String substring = audioPath.substring(0, lastIndexOf);
            String LyricPath=substring+".lrc";
            File file=new File(LyricPath);
            if(!file.exists()) {
                Toast.makeText(SystemAudioActivity.this, "未找到歌词文件", Toast.LENGTH_SHORT).show();
                lyrics=new ArrayList<>();
                Lyric lyric=new Lyric();
                //集合中装载数据
                for(int i = 0; i < 1; i++) {
                    lyric.setContent("歌词未找到");
                    lyric.setShowTime(1000*i);
                    lyric.setSleepTime(1500+i);
                    lyrics.add(lyric);
                    lyric=new Lyric();
                }
                showLyricView.setLyrics(lyrics);
                return;

            }
                lyricUtils.readLyricFile(file);
                if(lyricUtils.isExistsLyric()) {
                    lyrics = lyricUtils.getLyrics();
                    showLyricView.setLyrics(lyrics);
                }
        }
        //找到歌词所对应的路径
        //解析，装进歌词显示集合中
        //发消息实时跟新歌词
        //根据当前进度，实时显示歌词
        handler.sendEmptyMessage(UPDATA_LYRIC_AMONGTIME);//发送消息开始实时更新歌词
    }
    private void bindandStartService() {
        musicIntent=new Intent(this, MusicPlayerService.class);
        musicIntent.setAction("com.example.yaphet.phone_audio_radio1.service.MusicPlayerServiceADIL");
        bindService(musicIntent,connection,BIND_AUTO_CREATE);
        startService(musicIntent);//用处不用实例化多个服务
    }
    private void getPosition() {
        notification = getIntent().getBooleanExtra("notification", false);
        if(!notification) {//来自于列表
            passedPotion = getIntent().getIntExtra("position", 0);
        }else{
        }
    }
    private void setDrawable() {
        ivIcon.setImageResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivIcon.getDrawable();
        animationDrawable.start();
    }
    @OnClick({R.id.btn_audio_playmode, R.id.btn_audio_pre, R.id.btn_audio_start_pause, R.id.btn_audio_next, R.id.btn_lyrc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_audio_playmode:
                //设置播放模式
                setMusicPlayMode();
                break;
            case R.id.btn_audio_pre:
                if(playerBinder!=null) {
                    playerBinder.pre();
                }
                break;
            case R.id.btn_audio_start_pause:
                //判断是否在播放状态，然后进行设置相应的播放暂停，以及设置相应的图片变化背景
                if(playerBinder.isPlayering()) {//在播放
                    //暂停，设置背景
                    playerBinder.pause();
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                }else {//在暂停状态
                    playerBinder.start();
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
                break;
            case R.id.btn_audio_next:
                if(playerBinder!=null) {
                playerBinder.next();
                }

                break;
            case R.id.btn_lyrc:
                break;
        }
    }
    private void checkMusicMode(){
        int playMode = playerBinder.getPlayMode();
        //设置按钮的状态
        if(playMode== MusicPlayerService.REPEAT_NORMAL) {
            btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
        }else if(playMode==MusicPlayerService.REPEAT_SINGLE) {
            btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
        }else if(playMode==MusicPlayerService.REPEAT_ALL) {
            btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
        }
        //校验播放和暂停的按钮
        if(playerBinder.isPlayering()) {
            btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
        }else{
            btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
        }
    }
    private void setMusicPlayMode() {
        int playMode = playerBinder.getPlayMode();//读取当前的播放模式
        if(playMode== MusicPlayerService.REPEAT_NORMAL) {
            //把播放模式设置为单曲循环
            playMode=MusicPlayerService.REPEAT_SINGLE;
        }else if(playMode==MusicPlayerService.REPEAT_SINGLE) {
            //把播放模式设置为循环全部
            playMode=MusicPlayerService.REPEAT_ALL;

        }else if(playMode==MusicPlayerService.REPEAT_ALL) {
            //把播放模式设置为顺序播放
            playMode=MusicPlayerService.REPEAT_NORMAL;
        }
        playerBinder.setPlayMode(playMode);
        //SavaWordNet.putMusicMode(MyApplication.getContext(),"musicmodle",playMode);//在文件中保存数据
        //设置按钮的状态
        if(playMode== MusicPlayerService.REPEAT_NORMAL) {
            btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            Toast.makeText(SystemAudioActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
        }else if(playMode==MusicPlayerService.REPEAT_SINGLE) {
            btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            Toast.makeText(SystemAudioActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
        }else if(playMode==MusicPlayerService.REPEAT_ALL) {
          btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            Toast.makeText(SystemAudioActivity.this, "全部播放", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        Log.e("tagggg","onDestroy------");
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);//不移除会有性能上的损失
        if(viewReceiver!=null) {
            unregisterReceiver(viewReceiver);
        }
        if(connection!=null) {//解绑服务
            unbindService(connection);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case  1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    isGriand=true;

                }else{
                    Toast.makeText(MyApplication.getContext(), "你取消了授权", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private class MyAudioSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                playerBinder.Seekto(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
