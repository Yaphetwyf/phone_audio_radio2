package com.example.yaphet.phone_audio_radio1.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;


/**
 * Created by WYF on 2017/12/14.
 */

public class VitamioVideoView extends io.vov.vitamio.widget.VideoView{
    public VitamioVideoView(Context context) {
        this(context,null);
    }

    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VitamioVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     * 自定义的设置屏幕宽高
     * @param videowidth
     * @param vidioheight
     */
    public void setVideoSize(int videowidth,int vidioheight){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height=vidioheight;
        layoutParams.width=videowidth;
        setLayoutParams(layoutParams);
    }
}
