package com.example.yaphet.phone_audio_radio1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.domin.RadioInfo;
import com.example.yaphet.phone_audio_radio1.util.Utils;
import com.example.yaphet.phone_audio_radio1.view.VitamioVideoView;
import com.example.yaphet.phone_audio_radio1.view.VitamioVideoViewTest;


import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

public class SystemVatamioActivity extends Activity {
    private static final int NET_SPEED = 4;
    @Bind(R.id.loading_net_text)
    TextView loading_net_text;
    @Bind(R.id.loading_net)
    LinearLayout loadingnet;
    @Bind(R.id.Plyer_buffer)
    LinearLayout PlayBuffer;
    @Bind(R.id.Buffer_net)
    TextView BufferNet;
    private List<RadioInfo> radioInfos;
    private BattorychangeReceiver battorychangeReceiver;
    int position;
    private final int UPDATA_SEEKBAR = 1;
    private Utils utils;
    private Uri uri;
    @Bind(R.id.radio_controler)
    RelativeLayout relativeLayoutall;
    @Bind(R.id.Vv_Vitamiovidio_system)
    VitamioVideoViewTest VvVidioSystem;
    @Bind(R.id.tv_topsimple_radioname)
    TextView tvTopsimpleRadioname;
    @Bind(R.id.battary_radio)
    ImageView battaryRadio;
    @Bind(R.id.system_time)
    TextView systemTime;
    @Bind(R.id.voice_control)
    Button voiceControl;
    @Bind(R.id.voice_seekbar)
    SeekBar voiceSeekbar;
    @Bind(R.id.switch_player)
    Button switchPlayer;
    @Bind(R.id.now_voido_player)
    TextView nowVoidoPlayer;
    @Bind(R.id.totle_radio_play)
    TextView totleRadioPlay;
    @Bind(R.id.play_seekbar)
    SeekBar play_seekbar;
    @Bind(R.id.back)
    Button back;
    @Bind(R.id.last)
    Button last;
    @Bind(R.id.playorpause)
    Button playorpause;
    @Bind(R.id.next)
    Button next;
    @Bind(R.id.big)
    Button big;
    private final int UPDATE_AUTO = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NET_SPEED://更新网速
                    String netSpeed = utils.showNetSpeed();
                    BufferNet.setText("玩命加载中..."+netSpeed);
                    loading_net_text.setText("玩命加载中..."+netSpeed);
                    handler.removeMessages(NET_SPEED);
                    handler.sendEmptyMessageDelayed(NET_SPEED, 2000);
                    break;
                case UPDATA_SEEKBAR:
                    int currentPosition = (int) VvVidioSystem.getCurrentPosition();
                    play_seekbar.setProgress(currentPosition);
                    nowVoidoPlayer.setText(utils.stringForTime(currentPosition));
                    String getsystemtime = Getsystemtime();
                    systemTime.setText(getsystemtime);

                    //缓冲进度跟新，也是一秒钟更新一次
                    if (fromNet) {
                        //来源于网络，缓冲资源
                        int bufferPercentage = VvVidioSystem.getBufferPercentage();//获得缓存的百分比1--100的值
                        int buffer = bufferPercentage * play_seekbar.getMax();
                        int secondProgress = buffer / 100;
                        play_seekbar.setSecondaryProgress(secondProgress);
                    }
                    handler.removeMessages(UPDATA_SEEKBAR);
                    handler.sendEmptyMessageDelayed(UPDATA_SEEKBAR, 1000);
                    break;
                case ISSHOW_HIDE:
                    hideRadioControler();
                    break;
            }
        }
    };
    private final int ISSHOW_HIDE = 3;

    private String Getsystemtime() {//得到系统时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String data = simpleDateFormat.format(new Date());
        return data;
    }

    private GestureDetector gestureDetector;
    private int videoHeight;
    private int videoWidth;

    //控制音量
    AudioManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);
        setContentView(R.layout.activity_vatamio_radio);
        ButterKnife.bind(this);
        initData();
        hideRadioControler();//默认情况下，隐藏标题栏
        getRadiopath();
        initVedioViewListener();
        // VvVidioSystem.setMediaController(new MediaController(this));//设置系统的播放controler
    }
    @Override
    protected void onResume() {
        super.onResume();
        //可以在onResume方法中实例化手势识别器
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //Toast.makeText(SystemRadioActivity.this, "被点击了", Toast.LENGTH_SHORT).show();
                //监听点击事件
                if (isshow == false) {
                    hideRadioControler();
                } else if (isshow == true) {
                    handler.removeMessages(ISSHOW_HIDE);
                    showRadioControler();
                    handler.sendEmptyMessageDelayed(ISSHOW_HIDE, 5000);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return super.onSingleTapUp(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Toast.makeText(SystemVatamioActivity.this, "被长按了", Toast.LENGTH_SHORT).show();
                StartandPause();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                VideoSizedefaultToFull();
                return super.onDoubleTap(e);
            }
        });
    }

    //判断是否播放的是网络视频
    private Boolean fromNet(String uri) {
        Boolean isFromNet = false;
        if (uri != null) {
            if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
                //以上说明是网络视频
                isFromNet = true;
            } else {
                //是本地视频
                isFromNet = false;
            }
        }
        return isFromNet;
    }
    Boolean fromNet;
    private void getRadiopath() {
        uri = getIntent().getData();//得到播放地址
        radioInfos = (List<RadioInfo>) getIntent().getSerializableExtra("vadio_list");
        position = getIntent().getIntExtra("position", 0);
        if (radioInfos != null && radioInfos.size() > 0) {//按照视频列表播放
            RadioInfo radioInfo = radioInfos.get(position);
            fromNet = fromNet(radioInfo.getAdressAbso());
            VvVidioSystem.setVideoPath(radioInfo.getAdressAbso());
            tvTopsimpleRadioname.setText(radioInfo.getDisplayName());
        } else if (uri != null) {
            fromNet = fromNet(uri.toString());
            VvVidioSystem.setVideoURI(uri);
            tvTopsimpleRadioname.setText(uri.toString());//视频讲错，播放地址uri转为为字符串也没用
        } else {
            Toast.makeText(SystemVatamioActivity.this, "视频列表为空", Toast.LENGTH_SHORT).show();
        }
        setButtonstate();
    }
    private int streamVolume;
    private int streamMaxVolume;

    private void initData() {
        //设置音量
        manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        streamVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);//获取设备当前音量
        streamMaxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获得最大的音量
        voiceSeekbar.setMax(streamMaxVolume);
        voiceSeekbar.setProgress(streamVolume);

        utils = new Utils();
        //用广播接收器监听电量变化
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        battorychangeReceiver = new BattorychangeReceiver();
        registerReceiver(battorychangeReceiver, intentFilter);

        //检测网速
        handler.sendEmptyMessage(NET_SPEED);
    }
    private void initVedioViewListener() {
        voiceSeekbar.setOnSeekBarChangeListener(new VoiceseekBarListemner());
        play_seekbar.setOnSeekBarChangeListener(new SeekListener());
        VvVidioSystem.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        VvVidioSystem.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoHeight = mp.getVideoHeight();
                videoWidth = mp.getVideoWidth();
                VvVidioSystem.start();
                //更新Seekbar功能
                //当准备好的时候
                int duration = (int) VvVidioSystem.getDuration();//得到文件的时长
                play_seekbar.setMax(duration);//使文件总时长和SEEKbar的最大长度相关联
                nowVoidoPlayer.setText("00:00");//当准备好的时候就调用VvVidioSystem或者mp的getcurrentposition会出现找不到文件的错误
                totleRadioPlay.setText(utils.stringForTime(duration));//使文本显示文件的总时长
                handler.sendEmptyMessage(UPDATA_SEEKBAR);//发消息之后在主线程中更新进度

                VideoSizedefaultToFull();//默认情况下为默认的屏幕大小
                loadingnet.setVisibility(View.GONE);

                mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        return false;
                    }
                });
            }
        });
        VvVidioSystem.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(radioInfos!=null) {
                    if ((position < (radioInfos.size() - 1))) {
                        Toast.makeText(SystemVatamioActivity.this, "即将播放下一个视频", Toast.LENGTH_SHORT).show();
                        PlayNextitem();
                    } else {
                        Toast.makeText(SystemVatamioActivity.this, "没有下一个视频了！！！", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        VvVidioSystem.setOnInfoListener(new MyInfoListener());
    }

    @OnClick({R.id.playorpause, R.id.back, R.id.last, R.id.next, R.id.big, R.id.voice_control})
    void ControlRadio(View view) {
        switch (view.getId()) {
            case R.id.playorpause:
                StartandPause();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.last:
                playLastitem();
                break;
            case R.id.next:
                PlayNextitem();
                break;
            case R.id.big:
                //屏幕由默认尺寸到全屏,刚进来时为默认
                //改变Button的状态
                VideoSizedefaultToFull();
                break;
            case R.id.voice_control:
                isclose = !isclose;
                UpdataVoice(streamVolume, isclose);
                break;
            case R.id.switch_player:
                //开启万能播放器
                showSwichPlayerDialog();
                break;
            default:
                break;
        }
        handler.removeMessages(ISSHOW_HIDE);
        handler.sendEmptyMessageDelayed(ISSHOW_HIDE, 5000);
    }
    private void showSwichPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("万能播放器提醒您");
        builder.setMessage("当您播放一个视频，有花屏的是，可以尝试使用系统播放器播放");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSystemPlayer();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
    private void startSystemPlayer() {
        if(VvVidioSystem != null){
            VvVidioSystem.stopPlayback();
        }

        Intent intent = new Intent(this,SystemRadioActivity.class);
        if(radioInfos != null && radioInfos.size() > 0){

            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",(Serializable)radioInfos);
            intent.putExtras(bundle);
            intent.putExtra("position", position);

        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);

        finish();//关闭页面
    }

    private Boolean isclose = false;


    private Boolean isfull = false;

    private void VideoSizedefaultToFull() {
        if (isfull == false) {
            //屏幕尺寸为默认长度
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;
            int mVideoWidth = videoWidth;//代表视频的实际尺寸
            int mVideoHeight = videoHeight;

            if (mVideoWidth * height < width * mVideoHeight) {
                //Log.i("@@@", "image too wide, correcting");
                width = height * mVideoWidth / mVideoHeight;
            } else if (mVideoWidth * height > width * mVideoHeight) {
                //Log.i("@@@", "image too tall, correcting");
                height = width * mVideoHeight / mVideoWidth;
            }
            VvVidioSystem.setVideoSize(width, height);
            //改变Button状态
            big.setBackgroundResource(R.drawable.btn_full_screen_normal_seleter);
            isfull = true;
        } else if (isfull == true) {
            //屏幕尺寸为充满屏幕
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;
            VvVidioSystem.setVideoSize(width, height);
            //改变button的状态
            big.setBackgroundResource(R.drawable.btn_full_screen_normal);
            isfull = false;
        }
    }

    private void StartandPause() {
        if (VvVidioSystem.isPlaying()) {//如果正在播放，就停止，图标改为可以播放
            VvVidioSystem.pause();
            playorpause.setBackgroundResource(R.drawable.btn_play_normal_seleter);
        } else {//如果正在停止，按下之后就播放，图标改为可以停止
            VvVidioSystem.start();
            playorpause.setBackgroundResource(R.drawable.btn_pause_normal_seleter);
        }
    }

    private void playLastitem() {
        loadingnet.setVisibility(View.VISIBLE);
        position--;
        if (radioInfos != null && radioInfos.size() > 0) {
            //先让视频跳到下一个
            if (position >= 0) {
                RadioInfo radioInfo = radioInfos.get(position);
                fromNet = fromNet(radioInfo.getAdressAbso());
                VvVidioSystem.setVideoPath(radioInfo.getAdressAbso());
                tvTopsimpleRadioname.setText(radioInfo.getDisplayName());
            }
        }
        setButtonstate();
    }
    private void PlayNextitem() {
        loadingnet.setVisibility(View.VISIBLE);
        position++;//到下一个位置
        if (radioInfos != null && radioInfos.size() > 0) {
            //先让视频跳到下一个
            RadioInfo radioInfo = radioInfos.get(position);
            fromNet = fromNet(radioInfo.getAdressAbso());
            VvVidioSystem.setVideoPath(radioInfo.getAdressAbso());
            tvTopsimpleRadioname.setText(radioInfo.getDisplayName());
        }
        setButtonstate();
    }

    private void setButtonstate() {
        if (radioInfos != null && radioInfos.size() > 0) {
            if (radioInfos.size() == 1) {//如果只有一个
                next.setBackgroundResource(R.drawable.btn_next_gray);
                next.setEnabled(false);
                last.setBackgroundResource(R.drawable.btn_pre_gray);
                next.setEnabled(false);
            } else if (radioInfos.size() == 2) {
                if (position == 0) {
                    last.setBackgroundResource(R.drawable.btn_pre_gray);
                    last.setEnabled(false);
                    next.setBackgroundResource(R.drawable.btn_next_normal_selecter);
                    next.setEnabled(true);
                } else if (position == 1) {
                    next.setBackgroundResource(R.drawable.btn_next_gray);
                    next.setEnabled(false);
                    last.setBackgroundResource(R.drawable.btn_pre_normal_seleter);
                    last.setEnabled(true);
                }
            } else if (radioInfos.size() > 2) {
                if (position == 0) {
                    last.setBackgroundResource(R.drawable.btn_pre_gray);
                    last.setEnabled(false);
                } else if (position == (radioInfos.size() - 1)) {
                    Toast.makeText(SystemVatamioActivity.this, "这是最后一个视频", Toast.LENGTH_SHORT).show();
                    next.setBackgroundResource(R.drawable.btn_next_gray);
                    next.setEnabled(false);
                } else {//当列表元素大于2的时候，如果不是第一个或者是最后一个那就还原
                    next.setBackgroundResource(R.drawable.btn_next_normal_selecter);
                    next.setEnabled(true);
                    last.setBackgroundResource(R.drawable.btn_pre_normal_seleter);
                    last.setEnabled(true);
                }
            }
        } else if (uri != null) {
            //这种情况下只有一个文件
            //当只有一个文件的情况下,不能上一个或者下一个
            next.setBackgroundResource(R.drawable.btn_next_gray);
            next.setEnabled(false);
            last.setBackgroundResource(R.drawable.btn_pre_gray);
            next.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(battorychangeReceiver);
        super.onDestroy();
    }

    private class SeekListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当seekbar发生改变时调用此方法
         *
         * @param seekBar
         * @param progress 改变的
         * @param fromUser 是否是人为的改变，如果是则为true
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                VvVidioSystem.seekTo(progress);
            }
        }

        /**
         * 当手机触碰时调用此方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            handler.removeMessages(ISSHOW_HIDE);
        }

        /**
         * 当手指结束触碰时调用此方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(ISSHOW_HIDE, 5000);
        }
    }

    private class BattorychangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_OKAY)) {//当电池充满时
                Toast.makeText(SystemVatamioActivity.this, "电池已充满，请放心使用", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                Toast.makeText(SystemVatamioActivity.this, "电池电量低，请充电", Toast.LENGTH_SHORT).show();
            }
            int level = intent.getIntExtra("level", 0);
            if (level <= 0) {
                battaryRadio.setImageResource(R.drawable.ic_battery_0);
            } else if (level <= 10) {
                battaryRadio.setImageResource(R.drawable.ic_battery_10);
            } else if (level <= 20) {
                battaryRadio.setImageResource(R.drawable.ic_battery_20);
            } else if (level <= 40) {
                battaryRadio.setImageResource(R.drawable.ic_battery_40);
            } else if (level <= 60) {
                battaryRadio.setImageResource(R.drawable.ic_battery_60);
            } else if (level <= 80) {
                battaryRadio.setImageResource(R.drawable.ic_battery_80);
            } else if (level <= 100) {
                battaryRadio.setImageResource(R.drawable.ic_battery_100);
            }
        }
    }

    private float startY;
    private int currentVoice;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        //设置上下滑动屏幕改变声音大小
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录初始位置，记录初始音量,移除自动消失的消息
                startY = event.getY();
                currentVoice = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(ISSHOW_HIDE);
                break;
            case MotionEvent.ACTION_MOVE:
                //获得最终的位置，得到移动的位置
                float endY = event.getY();
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int height = metrics.heightPixels;
                int width = metrics.widthPixels;//获得屏幕的宽高
                float move = startY - endY;
                int changeVoice = (int) (move * streamMaxVolume / Math.min(height, width));
                int voice = Math.min(streamMaxVolume, Math.max(0, changeVoice + streamVolume));
                if (changeVoice != 0) {
                    isclose = false;
                    UpdataVoice(voice, isclose);
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(ISSHOW_HIDE, 5000);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private Boolean isshow = false;

    private void showRadioControler() {
        if (isshow == true) {
            relativeLayoutall.setVisibility(View.VISIBLE);
            isshow = false;
        }
    }

    private void hideRadioControler() {
        if (isshow == false) {
            relativeLayoutall.setVisibility(View.GONE);
            isshow = true;
        }
    }
    private class VoiceseekBarListemner implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //更新当前音量
                if (progress > 0) {
                    isclose = false;
                } else {
                    isclose = true;
                }
                UpdataVoice(progress, isclose);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(ISSHOW_HIDE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(ISSHOW_HIDE, 5000);
        }
    }
    private void UpdataVoice(int progress, Boolean closeVoice) {
        //更新音量
        if (closeVoice == false) {//不静音的时候
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);//第三个参数如果是1则代表调起系统的音量控制器
            voiceSeekbar.setProgress(progress);
            streamVolume = progress;
        } else {//静音的时候
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);//第三个参数如果是1则代表调起系统的音量控制器
            voiceSeekbar.setProgress(0);
        }
    }
    //监听物理键实现声音的放大缩小
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //缩小声音
            isshow = true;
            showRadioControler();
            handler.removeMessages(ISSHOW_HIDE);
            streamVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume--;
            isclose = false;
            UpdataVoice(streamVolume, isclose);
            handler.sendEmptyMessageDelayed(ISSHOW_HIDE, 5000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //开大声音
            isshow = true;
            showRadioControler();
            handler.removeMessages(ISSHOW_HIDE);
            streamVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
            streamVolume++;
            isclose = false;
            UpdataVoice(streamVolume, isclose);
            handler.sendEmptyMessageDelayed(ISSHOW_HIDE, 5000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private class MyInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始卡
                   // Toast.makeText(MyApplication.getContext(), "开始卡", Toast.LENGTH_SHORT).show();
                    PlayBuffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://结束卡
                    //Toast.makeText(MyApplication.getContext(), "结束卡", Toast.LENGTH_SHORT).show();
                    PlayBuffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }
}
