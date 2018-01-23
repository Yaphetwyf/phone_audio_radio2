package com.example.yaphet.phone_audio_radio1.domin;

/**
 * Created by WYF on 2018/1/11.
 * 歌词实体类
 */

public class Lyric {
    /**
     * 内容
     */
    private String content;
    /**
     * 高亮显示时间
     */
    private long sleepTime;
    /**
     * 现实的时间戳
     */
    private long showTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", sleepTime=" + sleepTime +
                ", showTime=" + showTime +
                '}';
    }
}
