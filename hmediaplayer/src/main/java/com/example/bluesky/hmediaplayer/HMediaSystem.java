package com.example.bluesky.hmediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by blue_sky on 2017/12/18.
 */

public class HMediaSystem extends HMediaInterface implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener{
    public MediaPlayer mediaPlayer;

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void prepare() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (dataSourceObjects.length > 1) {
                mediaPlayer.setLooping((boolean) dataSourceObjects[1]);
            }
            mediaPlayer.setOnPreparedListener(HMediaSystem.this);
            mediaPlayer.setOnCompletionListener(HMediaSystem.this);
            mediaPlayer.setOnBufferingUpdateListener(HMediaSystem.this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnSeekCompleteListener(HMediaSystem.this);
            mediaPlayer.setOnErrorListener(HMediaSystem.this);
            mediaPlayer.setOnInfoListener(HMediaSystem.this);
            mediaPlayer.setOnVideoSizeChangedListener(HMediaSystem.this);
            Class<MediaPlayer> clazz = MediaPlayer.class;
            Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
            if (dataSourceObjects.length > 2) {
                method.invoke(mediaPlayer, currentDataSource.toString(), dataSourceObjects[2]);
            } else {
                method.invoke(mediaPlayer, currentDataSource.toString(), null);
            }
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        mediaPlayer.seekTo((int) time);
    }

    @Override
    public void release() {
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    @Override
    public long getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public void setSurface(Surface surface) {
        mediaPlayer.setSurface(surface);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        if (currentDataSource.toString().toLowerCase().contains("mp3")) {
            HMediaManager.instance().mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (HVideoPlayerManager.getCurrentJzvd() != null) {
                        HVideoPlayerManager.getCurrentJzvd().onPrepared();
                    }
                }
            });
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        HMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (HVideoPlayerManager.getCurrentJzvd() != null) {
                    HVideoPlayerManager.getCurrentJzvd().onAutoCompletion();
                }
            }
        });
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, final int percent) {
        HMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (HVideoPlayerManager.getCurrentJzvd() != null) {
                    HVideoPlayerManager.getCurrentJzvd().setBufferProgress(percent);
                }
            }
        });
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        HMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (HVideoPlayerManager.getCurrentJzvd() != null) {
                    HVideoPlayerManager.getCurrentJzvd().onSeekComplete();
                }
            }
        });
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, final int what, final int extra) {
        HMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (HVideoPlayerManager.getCurrentJzvd() != null) {
                    HVideoPlayerManager.getCurrentJzvd().onError(what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, final int what, final int extra) {
        HMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (HVideoPlayerManager.getCurrentJzvd() != null) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        HVideoPlayerManager.getCurrentJzvd().onPrepared();
                    } else {
                        HVideoPlayerManager.getCurrentJzvd().onInfo(what, extra);
                    }
                }
            }
        });
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        HMediaManager.instance().currentVideoWidth = width;
        HMediaManager.instance().currentVideoHeight = height;
        HMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (HVideoPlayerManager.getCurrentJzvd() != null) {
                    HVideoPlayerManager.getCurrentJzvd().onVideoSizeChanged();
                }
            }
        });
    }
}
