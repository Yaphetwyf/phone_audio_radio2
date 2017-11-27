package com.example.yaphet.phone_audio_radio1.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemRadioActivity extends Activity {
    private int UPDATA_SEEKBAR=1;
    private Utils utils;
    @Bind(R.id.radio_controler)
    RelativeLayout relativeLayoutall;
    @Bind(R.id.Vv_vidio_system)
    VideoView VvVidioSystem;
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
    private int UPDATE_AUTO=2;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentPosition = VvVidioSystem.getCurrentPosition();
            play_seekbar.setProgress(currentPosition);
            nowVoidoPlayer.setText(utils.stringForTime(currentPosition));
            handler.removeMessages(UPDATA_SEEKBAR);
            handler.sendEmptyMessageDelayed(UPDATE_AUTO,1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_radio);
        ButterKnife.bind(this);
        utils=new Utils();
        Uri uri = getIntent().getData();//得到播放地址
        if (uri != null) {
            VvVidioSystem.setVideoURI(uri);
        }
        initVedioViewListener();
        // VvVidioSystem.setMediaController(new MediaController(this));//设置系统的播放controler
    }

    private void initVedioViewListener() {
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
                VvVidioSystem.start();
                //更新Seekbar功能
                //当准备好的时候
                int duration = VvVidioSystem.getDuration();//得到文件的时长
                play_seekbar.setMax(duration);//使文件总时长和SEEKbar的最大长度相关联
                nowVoidoPlayer.setText("00:00");//当准备好的时候就调用VvVidioSystem或者mp的getcurrentposition会出现找不到文件的错误
                totleRadioPlay.setText(utils.stringForTime(duration));//使文本显示文件的总时长
               handler.sendEmptyMessage(UPDATA_SEEKBAR);//发消息之后在主线程中更新进度
            }
        });
        VvVidioSystem.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playorpause.setBackgroundResource(R.drawable.btn_play_normal_seleter);
                Toast.makeText(SystemRadioActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.playorpause,R.id.back,R.id.last,R.id.next,R.id.big})
    void ControlRadio(View view){
        switch (view.getId()) {
            case R.id.playorpause :
                if(VvVidioSystem.isPlaying()) {//如果正在播放，就停止，图标改为可以播放
                    VvVidioSystem.pause();
                    playorpause.setBackgroundResource(R.drawable.btn_play_normal_seleter);
                }else {//如果正在停止，按下之后就播放，图标改为可以停止
                    VvVidioSystem.start();
                    playorpause.setBackgroundResource(R.drawable.btn_pause_normal_seleter);
                }

                break;
            default:
                break;
        }
    }
    private class SeekListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当seekbar发生改变时调用此方法
         * @param seekBar
         * @param progress   改变的
         * @param fromUser   是否是人为的改变，如果是则为true
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                VvVidioSystem.seekTo(progress);
            }
        }

        /**
         * 当手机触碰时调用此方法
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * 当手指结束触碰时调用此方法
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
