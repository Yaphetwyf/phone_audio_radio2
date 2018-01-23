package com.example.yaphet.phone_audio_radio1.util;

import android.net.TrafficStats;
import android.os.Message;

import java.util.Formatter;
import java.util.Locale;

/**
 * 把毫秒转化成20:09几分几秒的转换格式
 */
public class Utils {

	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;

	public Utils() {
		// 转换成字符串的时间
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

	}

	/**
	 * 把毫秒转换成：1:20:30这里形式
	 * @param timeMs
	 * @return
	 */
	public String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int seconds = totalSeconds % 60;

		int minutes = (totalSeconds / 60) % 60;

		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public String showNetSpeed() {

        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        String speedNet = String.valueOf(speed) + " kb/s";
        return speedNet;

    }
    private long getTotalRxBytes() {
    return TrafficStats.getUidRxBytes(MyApplication.getContext().getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
    }

	/**
	 * 把秒时间转换成00:00：00的格式
	 * @param timeSec
	 * @return
	 */
	public String stringForTimefors(int timeSec) {
		int totalSeconds = timeSec;
		int seconds = totalSeconds % 60;
		int minutes = totalSeconds / 60 % 60;
		int hours = totalSeconds / 3600;
		if (null!=mFormatBuilder){
			this.mFormatBuilder.setLength(0);
		}
		return hours > 0 ? this.mFormatter.format("%d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)}).toString() : this.mFormatter.format("%02d:%02d", new Object[]{Integer.valueOf(minutes), Integer.valueOf(seconds)}).toString();
	}
}
