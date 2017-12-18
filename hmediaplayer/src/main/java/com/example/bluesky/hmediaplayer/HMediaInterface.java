package com.example.bluesky.hmediaplayer;

import android.view.Surface;

/**
 * Created by blue_sky on 2017/12/18.
 * 自定义播放器
 */

public abstract class HMediaInterface {
    public Object currentDataSource;//正在播放的当前url或uri
    /**
     * 第一个是url的map
     * 第二个是loop
     * 第三个是header
     * 第四个是context
     */
    public Object[] dataSourceObjects;//包含了地址的map（多分辨率用），context，loop，header等

    public abstract void start();

    public abstract void prepare();

    public abstract void pause();

    public abstract boolean isPlaying();

    public abstract void seekTo(long time);

    public abstract void release();

    public abstract long getCurrentPosition();

    public abstract long getDuration();

    public abstract void setSurface(Surface surface);
}
