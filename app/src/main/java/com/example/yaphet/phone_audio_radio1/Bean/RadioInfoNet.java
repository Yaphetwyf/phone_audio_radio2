package com.example.yaphet.phone_audio_radio1.Bean;

import java.util.List;

/**
 * Created by WYF on 2017/10/29.
 */

public class RadioInfoNet {
    public RadioInfoNet(String movieName, String coverImg, String hightUrl, String videoTitle, int videoLength) {
        this.movieName = movieName;
        this.coverImg = coverImg;
        this.hightUrl = hightUrl;
        this.videoTitle = videoTitle;
        this.videoLength = videoLength;
    }

    public RadioInfoNet() {

    }

    /**
         * movieName : 《掠食城市》中文预告
         * coverImg : http://img5.mtime.cn/mg/2017/12/19/081113.68130060_120X90X4.jpg
         * url : http://vfx.mtime.cn/Video/2017/12/19/mp4/171219080949591626.mp4
         * hightUrl : http://vfx.mtime.cn/Video/2017/12/19/mp4/171219080949591626.mp4
         * videoTitle : 掠食城市：致命引擎 中文预告片
         * videoLength : 84
         */
        private String movieName;
        private String coverImg;
        private String hightUrl;
        private String videoTitle;
        private int videoLength;

        public String getMovieName() {
            return movieName;
        }

        public void setMovieName(String movieName) {
            this.movieName = movieName;
        }

        public String getCoverImg() {
            return coverImg;
        }

        public void setCoverImg(String coverImg) {
            this.coverImg = coverImg;
        }


        public String getHightUrl() {
            return hightUrl;
        }

        public void setHightUrl(String hightUrl) {
            this.hightUrl = hightUrl;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        public int getVideoLength() {
            return videoLength;
        }

        public void setVideoLength(int videoLength) {
            this.videoLength = videoLength;
        }

    @Override
    public String toString() {
        return "RadioInfoNet{" +
                "movieName='" + movieName + '\'' +
                ", coverImg='" + coverImg + '\'' +
                ", hightUrl='" + hightUrl + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoLength=" + videoLength +
                '}';
    }
}
