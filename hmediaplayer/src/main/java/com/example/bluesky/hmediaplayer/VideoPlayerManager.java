package com.example.bluesky.hmediaplayer;

/**
 * Created by blue_sky on 2017/12/18.
 * @author blue_sky
 */

public class VideoPlayerManager {
    public static VideoPlayer FIRST_FLOOR_HVD;
    public static VideoPlayer SECOND_FLOOR_HVD;

    public static VideoPlayer getFirstFloor() {
        return FIRST_FLOOR_HVD;
    }

    public static void setFirstFloor(VideoPlayer jzVideoPlayer) {
        FIRST_FLOOR_HVD = jzVideoPlayer;
    }

    public static VideoPlayer getSecondFloor() {
        return SECOND_FLOOR_HVD;
    }

    public static void setSecondFloor(VideoPlayer jzVideoPlayer) {
        SECOND_FLOOR_HVD = jzVideoPlayer;
    }

    public static VideoPlayer getCurrentHvd() {
        if (getSecondFloor() != null) {
            return getSecondFloor();
        }
        return getFirstFloor();
    }

    public static void completeAll() {
        if (SECOND_FLOOR_HVD != null) {
            SECOND_FLOOR_HVD.onCompletion();
            SECOND_FLOOR_HVD = null;
        }
        if (FIRST_FLOOR_HVD != null) {
            FIRST_FLOOR_HVD.onCompletion();
            FIRST_FLOOR_HVD = null;
        }
    }
}
