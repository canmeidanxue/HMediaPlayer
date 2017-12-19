package com.example.bluesky.hmediaplayer;

import android.view.Surface;

/**
 * Created by blue_sky on 2017/12/18.
 * 自定义播放器
 * @author blue_sky
 */

public abstract class BaseMediaInterface {
    /**
     *  正在播放的当前url或uri
     */
    public Object currentDataSource;
    /**
     * 第一个是url的map
     * 第二个是loop
     * 第三个是header
     * 第四个是context
     * 包含了地址的map（多分辨率用），context，loop，header等
     */
    public Object[] dataSourceObjects;

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
