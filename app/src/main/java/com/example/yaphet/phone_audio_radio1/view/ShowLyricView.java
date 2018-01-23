package com.example.yaphet.phone_audio_radio1.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.yaphet.phone_audio_radio1.domin.Lyric;
import com.example.yaphet.phone_audio_radio1.util.DensityUtil;

import java.util.ArrayList;

import static android.graphics.Paint.Align.CENTER;

/**
 * Created by WYF on 2018/1/11.
 * 自定义歌词显示控件
 */

public class ShowLyricView extends android.support.v7.widget.AppCompatTextView {
    private int ViewWidth;//视图的宽
    private int ViewHight;//视图的高
    //设置歌词列表
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    private float passShowLyric;
    private ArrayList<Lyric> lyrics=new ArrayList<>();;//装载歌词的集合
    private Paint paintcurrent;//绘制当前句的画笔
    private float everyItem=30;
    int proveindex=1;//辅助增加指数
    int reduceindex=1;//辅助减少指数
    float tempY = ViewHight / 2;//Y轴的中间坐标
    /**
     * 当前播放句的时间戳
     */
    long currentShowTime;
    /**
     * 当前播放句的高亮时间
     */
    long sleepTime;

    private int index=0;//当前句的索引，默认为0，现在假设为5
    private Paint paint;//绘制前后歌词的画笔

    public ShowLyricView(Context context) {
        this(context,null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initdata(context);
    }

    private void initdata(Context context) {//视图控件，数据等做初始化使用
        int dip2px = DensityUtil.dip2px(context, 20);
        everyItem=DensityUtil.dip2px(context,30);
        //设置当前句的画笔
        paintcurrent=new Paint();
        paintcurrent.setColor(Color.GREEN);
        paintcurrent.setAntiAlias(true);
        paintcurrent.setTextAlign(CENTER);
        paintcurrent.setTextSize(dip2px);

        //设置前后用的画笔
        paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextAlign(CENTER);//设置居中对齐
        paint.setTextSize(dip2px);

/*        Lyric lyric=new Lyric();
        //集合中装载数据
        for(int i = 0; i < 1; i++) {
            lyric.setContent("未找到歌词");
            lyric.setShowTime(1000*i);
            lyric.setSleepTime(1500+i);
            lyrics.add(lyric);
            lyric=new Lyric();
        }*/
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//得到视图的宽高
        super.onSizeChanged(w, h, oldw, oldh);
        ViewWidth=w;
        ViewHight=h;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(lyrics!=null&&lyrics.size()>0) {
            if(lyrics.size()>1) {
                float plush = 0;
                if(sleepTime ==0){
                    plush = 0;
                }else{
                    //平移
                    //这一句当前所花的时间 ：休眠时间 = 移动的距离 ： 总距离（行高）
                    //移动的距离 =  (这一句当前所花的时间 ：休眠时间)* 总距离（行高）
//                float delta = ((currentPositon-timePoint)/sleepTime )*textHeight;

                    //屏幕的的坐标 = 行高 + 移动的距离//可以是移动的距离，效果也还可以
                    plush = ((passShowLyric-currentShowTime)/sleepTime )*everyItem;
                }
                canvas.translate(0,-plush);
            }
            if(lyrics.size()==1) {
                index=0;
            }
            //绘制后面部分
            for(int i = index+1; i <lyrics.size() ; i++) {
                String nextContent = lyrics.get(i).getContent();//得到后面的现实的歌词
               if((ViewHight/2+everyItem*proveindex)<ViewHight) {
                    canvas.drawText(nextContent,ViewWidth/2,ViewHight/2+everyItem*proveindex,paint);
                }
                proveindex++;
            }
            // 绘制后面部分
/*            tempY = ViewHight / 2;//Y轴的中间坐标
            for (int i = index + 1; i < lyrics.size(); i++) {
                //每一句歌词
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + everyItem;
                if (tempY > ViewHight) {
                    break;
                }
                canvas.drawText(nextContent, ViewWidth / 2, tempY, paint);
            }*/
            //绘制当前部分
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent,ViewWidth/2,ViewHight/2,paintcurrent);
            float tempY = ViewHight / 2;//Y轴的中间坐标
            //绘制前面部分
            for(int i = index-1; i >=0 ; i--) {
                String preContent = lyrics.get(i).getContent();//得到后面的现实的歌词
                tempY = tempY - everyItem;
//                if((ViewHight/2-everyItem*reduceindex)>0) {
//                    canvas.drawText(preContent,ViewWidth/2,ViewHight/2-everyItem*reduceindex,paint);
//                }
                //reduceindex++;
                if (tempY > 0) {
                    canvas.drawText(preContent, ViewWidth / 2, tempY, paint);
                }
            }

        }else {
            canvas.drawText("未找到歌词",ViewWidth/2,ViewHight/2,paintcurrent);
        }
    }

    /**
     * 根据当前传入，计算出应该高亮那一句
     * @param showLyricPosition
     */
    public void setShowLyric(float showLyricPosition) {
        this.passShowLyric = showLyricPosition;//得到播放器播放上网时间
        if(lyrics!=null&&lyrics.size()>0) {//容错处理
            for(int i = 1; i <lyrics.size() ; i++) {//遍历得到时间戳
                long showTime = lyrics.get(i).getShowTime();//得到时间戳
                if(showTime>passShowLyric) {//截取左边的数组
                   int temp=i-1;
                    long showTime1 = lyrics.get(temp).getShowTime();
                    if(showTime1<passShowLyric) {
                        index=temp;
                        currentShowTime = lyrics.get(temp).getShowTime();//时间戳，则播放时间在这两个时间戳之间
                        sleepTime = lyrics.get(temp).getSleepTime();//得到需要高亮显示的时间
                    }
                }
            }
        }
            invalidate();
            proveindex=1;
        //postInvalidate();//子线程重新绘制
    }
}
