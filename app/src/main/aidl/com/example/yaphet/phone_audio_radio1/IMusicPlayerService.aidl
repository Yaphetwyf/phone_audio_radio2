// IMusicPlayerService.aidl
package com.example.yaphet.phone_audio_radio1;

// Declare any non-default types here with import statements

interface IMusicPlayerService {

    /**
     * 根据位置打开对应的音频文件,并且播放
     *
     * @param position
     */
     void openAudio(int position) ;
     /**
     * 播放音乐
     */
     void start();


    /**
     * 播暂停音乐
     */
     void pause();

    /**
     * 停止
     */
     void stop();

    /**
     * 得到当前的播放进度
     *
     * @return
     */
    int getCurrentPosition();

    /**
     * 得到当前音频的总时长
     *
     * @return
     */
     int getDuration();

    /**
     * 得到艺术家
     *
     * @return
     */
     String getArtist();

    /**
     * 得到歌曲名字
     *
     * @return
     */
     String getName();
 /**
     * 设置播放模式
     *
     * @param playmode
     */
     void setPlayMode(int playmode);

    /**
     * 得到播放模式
     *
     * @return
     */
     int getPlayMode();
     /**
         * 播放上一个音频
        */
      void pre();


    /**
     * 播放下一个音频
     */
     void next();
    /**
         * 得到歌曲播放的路径
         *
         * @return
         */
     String getAudioPath();

}
