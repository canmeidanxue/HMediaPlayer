package com.example.bluesky.hmediaplayer.modle;

import java.io.Serializable;

/**
 * Created on 2017/12/19.
 *
 * @author:037
 */

public class VideoInfo implements Serializable {
    /**
     * 视频url地址
     */
    private String url;
    /**
     * 播放视频的title
     */
    private String title;
    /**
     * 封面图片
     */
    private String imgUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
