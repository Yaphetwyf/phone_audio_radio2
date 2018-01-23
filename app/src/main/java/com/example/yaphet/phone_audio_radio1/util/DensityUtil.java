package com.example.yaphet.phone_audio_radio1.util;

/**
 * Created by WYF on 2017/9/20.
 */

import android.content.Context;

/**
 * 工具类：为了在任何大小的屏幕上完成适配，当你用的是绝对的像素dp时，为了在各个不同的屏幕上都能很好地显示。
 */
public class DensityUtil {
    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
