package com.example.bluesky.hmediaplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by blue_sky on 2017/12/18.
 *
 * @author blue_sky
 */

public abstract class VideoPlayer extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {
    public static final String TAG = "HVideoPlayer";
    /**
     * 最小滑动距离, 防止触碰到屏幕时弹出dialog
     */
    public static final int THRESHOLD = 80;
    /**
     * 更新时间和进度handler
     */
    private MyHandler myHandler = new MyHandler();
    /**
     * 更新时间和进度handler delley值
     */
    public static final int FULL_SCREEN_NORMAL_DELAY = 300;
    /**
     * 更新时间和进度handler  what值
     */
    private static final int WHAT_EXTRA = 1002;
    /**
     * 正常窗口标记
     */
    public static final int SCREEN_WINDOW_NORMAL = 0;
    /**
     * 集合显示是窗口标记
     */
    public static final int SCREEN_WINDOW_LIST = 1;
    /**
     * 全屏显示窗口标记
     */
    public static final int SCREEN_WINDOW_FULLSCREEN = 2;
    /**
     * 小窗口显示标记
     */
    public static final int SCREEN_WINDOW_TINY = 3;
    /**
     * 当前显示窗口状态为正常标记
     */
    public static final int CURRENT_STATE_NORMAL = 0;
    /**
     * 当前显示窗口状态为准备标记
     */
    public static final int CURRENT_STATE_PREPARING = 1;
    /**
     * 当前显示窗口状态为切换视频源标记
     */
    public static final int CURRENT_STATE_PREPARING_CHANGING_URL = 2;
    /**
     * 当前显示窗口状态为播放标记
     */
    public static final int CURRENT_STATE_PLAYING = 3;
    /**
     * 当前显示窗口状态为暂停标记
     */
    public static final int CURRENT_STATE_PAUSE = 5;
    /**
     * 当前显示窗口状态为播放完成标记
     */
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    /**
     * 当前显示窗口状态为播放出现错误标记
     */
    public static final int CURRENT_STATE_ERROR = 7;

    /**
     * 当播放的地址只有一个的时候的key
     */
    public static final String URL_KEY_DEFAULT = "URL_KEY_DEFAULT";
    /**
     * default
     */
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER = 0;
    /**
     * 视频封面显示标记
     */
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT = 1;
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP = 2;
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL = 3;
    /**
     * 是否显示action_bar, 默认是不显示
     */
    public static boolean ACTION_BAR_EXIST = true;
    /**
     * 是否显示tool_bar, 默认是不显示
     */
    public static boolean TOOL_BAR_EXIST = true;
    /**
     * 全屏显示
     */
    public static int FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
    /**
     * 竖屏显示
     */
    public static int NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    /**
     * 是否保存进度 默认是保存进度
     */
    public static boolean SAVE_PROGRESS = true;
    /**
     * 是否显示提示wifi是否连接, 默认是不出现
     */
    public static boolean WIFI_TIP_DIALOG_SHOWED = false;
    /**
     * 默认的视频封面显示type
     */
    public static int VIDEO_IMAGE_DISPLAY_TYPE = 0;
    /**
     * 点击退出全屏按钮标记
     */
    public static long CLICK_QUIT_FULLSCREEN_TIME = 0;
    /**
     * 记录上一次退出全屏的时间
     */
    public static long lastAutoFullscreenTime = 0;
    /**
     *
     */
    protected static UserAction USER_EVENT;
    /**
     * 记录当前播放状态
     */
    public int currentState = -1;
    /**
     * 记录当前播放窗口状态
     */
    public int currentScreen = -1;

    public Object[] objects = null;
    /**
     * seek 滑动位置
     */
    public long seekToInAdvance = 0;
    /**
     * 开始按钮
     */
    public ImageView startButton;
    /**
     * 播放进度条
     */
    public SeekBar progressBar;
    /**
     * 全屏按钮
     */
    public ImageView fullscreenButton;
    /**
     * 播放时间显示
     */
    public TextView currentTimeTextView, totalTimeTextView;
    /**
     * textureView渲染
     */
    public ViewGroup textureViewContainer;
    /**
     * 顶部与底部显示容器
     */
    public ViewGroup topContainer, bottomContainer;
    /**
     * 播放窗口宽度
     */
    public int widthRatio = 0;
    /**
     * 播放窗口高度
     */
    public int heightRatio = 0;
    /**
     * 这个参数原封不动直接通过MediaManager传给BaseMediaInterface。
     */
    public Object[] dataSourceObjects;
    public int currentUrlMapIndex = 0;
    public int positionInList = -1;
    public int videoRotation = 0;
    /**
     * 屏幕大小
     */
    protected int mScreenWidth;
    protected int mScreenHeight;
    /**
     * 音视频管理类
     */
    public AudioManager mAudioManager;
    protected Handler mHandler;
    /**
     * 是否正在拖动weekbar
     */
    protected boolean mTouchingProgressBar;
    /**
     * 点击位置x,y值
     */
    protected float mDownX;
    protected float mDownY;
    /**
     * 手势控制
     */
    protected boolean mChangeVolume;
    protected boolean mChangePosition;
    protected boolean mChangeBrightness;
    /**
     * 手势控制位置
     */
    protected long mGestureDownPosition;
    protected int mGestureDownVolume;
    protected float mGestureDownBrightness;
    /**
     * seekbar拖动位置保存
     */
    protected long mSeekTimePosition;
    /**
     * 是否直接返回
     */
    public boolean tmp_test_back = false;
    /**
     * 播放窗口管理类
     */
    public static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    releaseAllVideos();
                    Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    try {
                        if (MediaManager.isPlaying()) {
                            MediaManager.pause();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
                default:
                    break;
            }
        }
    };

    public VideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public static void releaseAllVideos() {
        if ((System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) > FULL_SCREEN_NORMAL_DELAY) {
            Log.d(TAG, "releaseAllVideos");
            VideoPlayerManager.completeAll();
            MediaManager.instance().positionInList = -1;
            MediaManager.instance().releaseMediaPlayer();
        }
    }

    public static void startFullscreen(Context context, Class _class, String url, Object... objects) {
        LinkedHashMap map = new LinkedHashMap();
        map.put(URL_KEY_DEFAULT, url);
        Object[] dataSourceObjects = new Object[1];
        dataSourceObjects[0] = map;
        startFullscreen(context, _class, dataSourceObjects, 0, objects);
    }

    public static void startFullscreen(Context context, Class _class, Object[] dataSourceObjects, int defaultUrlMapIndex, Object... objects) {
        hideSupportActionBar(context);
        Utils.setRequestedOrientation(context, FULLSCREEN_ORIENTATION);
        ViewGroup vp = (Utils.scanForActivity(context))
                .findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.jz_fullscreen_id);
        if (old != null) {
            vp.removeView(old);
        }
        try {
            Constructor<VideoPlayer> constructor = _class.getConstructor(Context.class);
            final VideoPlayer videoPlayer = constructor.newInstance(context);
            videoPlayer.setId(R.id.jz_fullscreen_id);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(videoPlayer, lp);
            videoPlayer.setUp(dataSourceObjects, defaultUrlMapIndex, VideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            videoPlayer.startButton.performClick();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean backPress() {
        Log.i(TAG, "backPress");
        if ((System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) < FULL_SCREEN_NORMAL_DELAY) {
            return false;
        }

        boolean isQuitFullscreen = VideoPlayerManager.getFirstFloor() != null &&
                (VideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN ||
                        VideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_TINY);

        if (VideoPlayerManager.getSecondFloor() != null) {
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            if (Utils.dataSourceObjectsContainsUri(VideoPlayerManager.getFirstFloor().dataSourceObjects, MediaManager.getCurrentDataSource())) {
                VideoPlayer videoPlayer = VideoPlayerManager.getSecondFloor();
                videoPlayer.onEvent(videoPlayer.currentScreen == VideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN ?
                        UserAction.ON_QUIT_FULLSCREEN :
                        UserAction.ON_QUIT_TINYSCREEN);
                VideoPlayerManager.getFirstFloor().playOnThisHvd();
            } else {
                quitFullscreenOrTinyWindow();
            }
            return true;
        } else if (isQuitFullscreen) {
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            quitFullscreenOrTinyWindow();
            return true;
        }
        return false;
    }

    public static void quitFullscreenOrTinyWindow() {
        //直接退出全屏和小窗
        VideoPlayerManager.getFirstFloor().clearFloatScreen();
        MediaManager.instance().releaseMediaPlayer();
        VideoPlayerManager.completeAll();
    }

    @SuppressLint("RestrictedApi")
    public static void showSupportActionBar(Context context) {
        if (ACTION_BAR_EXIST && Utils.getAppCompActivity(context) != null) {
            ActionBar ab = Utils.getAppCompActivity(context).getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.show();
            }
        }
        if (TOOL_BAR_EXIST) {
            Utils.getWindow(context).clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @SuppressLint("RestrictedApi")
    public static void hideSupportActionBar(Context context) {
        if (ACTION_BAR_EXIST && Utils.getAppCompActivity(context) != null) {
            ActionBar ab = Utils.getAppCompActivity(context).getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.hide();
            }
        }
        if (TOOL_BAR_EXIST) {
            Utils.getWindow(context).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void clearSavedProgress(Context context, String url) {
        Utils.clearSavedProgress(context, url);
    }

    public static void setHUserAction(UserAction jzUserEvent) {
        USER_EVENT = jzUserEvent;
    }

    public static void goOnPlayOnResume() {
        if (VideoPlayerManager.getCurrentHvd() != null) {
            VideoPlayer jzvd = VideoPlayerManager.getCurrentHvd();
            if (jzvd.currentState == VideoPlayer.CURRENT_STATE_PAUSE) {
                jzvd.onStatePlaying();
                MediaManager.start();
            }
        }
    }

    public static void goOnPlayOnPause() {
        if (VideoPlayerManager.getCurrentHvd() != null) {
            VideoPlayer jzvd = VideoPlayerManager.getCurrentHvd();
            if (jzvd.currentState == VideoPlayer.CURRENT_STATE_AUTO_COMPLETE ||
                    jzvd.currentState == VideoPlayer.CURRENT_STATE_NORMAL) {
            } else {
                jzvd.onStatePause();
                MediaManager.pause();
            }
        }
    }

    public static void onScrollAutoTiny(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        int currentPlayPosition = MediaManager.instance().positionInList;
        if (currentPlayPosition >= 0) {
            if ((currentPlayPosition < firstVisibleItem || currentPlayPosition > (lastVisibleItem - 1))) {
                if (VideoPlayerManager.getCurrentHvd() != null &&
                        VideoPlayerManager.getCurrentHvd().currentScreen != VideoPlayer.SCREEN_WINDOW_TINY) {
                    if (VideoPlayerManager.getCurrentHvd().currentState == VideoPlayer.CURRENT_STATE_PAUSE) {
                        VideoPlayer.releaseAllVideos();
                    } else {
                        Log.e(TAG, "onScroll: out screen");
                        VideoPlayerManager.getCurrentHvd().startWindowTiny();
                    }
                }
            } else {
                if (VideoPlayerManager.getCurrentHvd() != null &&
                        VideoPlayerManager.getCurrentHvd().currentScreen == VideoPlayer.SCREEN_WINDOW_TINY) {
                    Log.e(TAG, "onScroll: into screen");
                    VideoPlayer.backPress();
                }
            }
        }
    }

    public static void onScrollReleaseAllVideos(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        int currentPlayPosition = MediaManager.instance().positionInList;
        if (currentPlayPosition >= 0) {
            if ((currentPlayPosition < firstVisibleItem || currentPlayPosition > (lastVisibleItem - 1))) {
                VideoPlayer.releaseAllVideos();
            }
        }
    }

    public static void onChildViewAttachedToWindow(View view, int jzvdId) {
        if (VideoPlayerManager.getCurrentHvd() != null && VideoPlayerManager.getCurrentHvd().currentScreen == VideoPlayer.SCREEN_WINDOW_TINY) {
            VideoPlayer videoPlayer = view.findViewById(jzvdId);
            if (videoPlayer != null && Utils.getCurrentFromDataSource(videoPlayer.dataSourceObjects, videoPlayer.currentUrlMapIndex).equals(MediaManager.getCurrentDataSource())) {
                VideoPlayer.backPress();
            }
        }
    }

    public static void onChildViewDetachedFromWindow(View view) {
        if (VideoPlayerManager.getCurrentHvd() != null && VideoPlayerManager.getCurrentHvd().currentScreen != VideoPlayer.SCREEN_WINDOW_TINY) {
            VideoPlayer videoPlayer = VideoPlayerManager.getCurrentHvd();
            if (((ViewGroup) view).indexOfChild(videoPlayer) != -1) {
                if (videoPlayer.currentState == VideoPlayer.CURRENT_STATE_PAUSE) {
                    VideoPlayer.releaseAllVideos();
                } else {
                    videoPlayer.startWindowTiny();
                }
            }
        }
    }

    public static void setTextureViewRotation(int rotation) {
        if (MediaManager.textureView != null) {
            MediaManager.textureView.setRotation(rotation);
        }
    }

    public static void setVideoImageDisplayType(int type) {
        VideoPlayer.VIDEO_IMAGE_DISPLAY_TYPE = type;
        if (MediaManager.textureView != null) {
            MediaManager.textureView.requestLayout();
        }
    }

    public Object getCurrentUrl() {
        return Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex);
    }

    public abstract int getLayoutId();

    public void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        startButton = findViewById(R.id.start);
        fullscreenButton = findViewById(R.id.fullscreen);
        progressBar = findViewById(R.id.bottom_seek_progress);
        currentTimeTextView = findViewById(R.id.current);
        totalTimeTextView = findViewById(R.id.total);
        bottomContainer = findViewById(R.id.layout_bottom);
        textureViewContainer = findViewById(R.id.surface_container);
        topContainer = findViewById(R.id.layout_top);

        startButton.setOnClickListener(this);
        fullscreenButton.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        bottomContainer.setOnClickListener(this);
        textureViewContainer.setOnClickListener(this);
        textureViewContainer.setOnTouchListener(this);

        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mHandler = new Handler();

        try {
            if (isCurrentPlay()) {
                NORMAL_ORIENTATION = ((AppCompatActivity) context).getRequestedOrientation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp(String url, int screen, Object... objects) {
        LinkedHashMap map = new LinkedHashMap();
        map.put(URL_KEY_DEFAULT, url);
        Object[] dataSourceObjects = new Object[1];
        dataSourceObjects[0] = map;
        setUp(dataSourceObjects, 0, screen, objects);
    }

    public void setUp(Object[] dataSourceObjects, int defaultUrlMapIndex, int screen, Object... objects) {
        if (this.dataSourceObjects != null && Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) != null &&
                Utils.getCurrentFromDataSource(this.dataSourceObjects, currentUrlMapIndex).equals(Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex))) {
            return;
        }
        if (isCurrentPlayer() && Utils.dataSourceObjectsContainsUri(dataSourceObjects, MediaManager.getCurrentDataSource())) {
            long position = 0;
            try {
                position = MediaManager.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (position != 0) {
                Utils.saveProgress(getContext(), MediaManager.getCurrentDataSource(), position);
            }
            MediaManager.instance().releaseMediaPlayer();
        } else if (isCurrentPlayer() && !Utils.dataSourceObjectsContainsUri(dataSourceObjects, MediaManager.getCurrentDataSource())) {
            startWindowTiny();
        } else if (!isCurrentPlayer() && Utils.dataSourceObjectsContainsUri(dataSourceObjects, MediaManager.getCurrentDataSource())) {
            if (VideoPlayerManager.getCurrentHvd() != null &&
                    VideoPlayerManager.getCurrentHvd().currentScreen == VideoPlayer.SCREEN_WINDOW_TINY) {
                //需要退出小窗退到我这里，我这里是第一层级
                tmp_test_back = true;
            }
        } else if (!isCurrentPlayer() && !Utils.dataSourceObjectsContainsUri(dataSourceObjects, MediaManager.getCurrentDataSource())) {
        }
        this.dataSourceObjects = dataSourceObjects;
        this.currentUrlMapIndex = defaultUrlMapIndex;
        this.currentScreen = screen;
        this.objects = objects;
        onStateNormal();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.start) {
            Log.i(TAG, "onClick start [" + this.hashCode() + "] ");
            if (dataSourceObjects == null || Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentState == CURRENT_STATE_NORMAL) {
                if (!Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("file") && !
                        Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("/") &&
                        !Utils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    showWifiDialog(UserAction.ON_CLICK_START_ICON);
                    return;
                }
                startVideo();
                onEvent(UserAction.ON_CLICK_START_ICON);
            } else if (currentState == CURRENT_STATE_PLAYING) {
                onEvent(UserAction.ON_CLICK_PAUSE);
                Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
                MediaManager.pause();
                onStatePause();
            } else if (currentState == CURRENT_STATE_PAUSE) {
                onEvent(UserAction.ON_CLICK_RESUME);
                MediaManager.start();
                onStatePlaying();
            } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                onEvent(UserAction.ON_CLICK_START_AUTO_COMPLETE);
                startVideo();
            }
        } else if (i == R.id.fullscreen) {
            Log.i(TAG, "onClick fullscreen [" + this.hashCode() + "] ");
            if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                return;
            }
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                //quit fullscreen
                backPress();
            } else {
                Log.d(TAG, "toFullscreenActivity [" + this.hashCode() + "] ");
                onEvent(UserAction.ON_ENTER_FULLSCREEN);
                startWindowFullscreen();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "onTouch surfaceContainer actionDown [" + this.hashCode() + "] ");
                    mTouchingProgressBar = true;

                    mDownX = x;
                    mDownY = y;
                    mChangeVolume = false;
                    mChangePosition = false;
                    mChangeBrightness = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "onTouch surfaceContainer actionMove [" + this.hashCode() + "] ");
                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                        if (!mChangePosition && !mChangeVolume && !mChangeBrightness) {
                            if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                                cancelProgressTimer();
                                if (absDeltaX >= THRESHOLD) {
                                    // 全屏模式下的CURRENT_STATE_ERROR状态下,不响应进度拖动事件.
                                    // 否则会因为mediaplayer的状态非法导致App Crash
                                    if (currentState != CURRENT_STATE_ERROR) {
                                        mChangePosition = true;
                                        mGestureDownPosition = getCurrentPositionWhenPlaying();
                                    }
                                } else {
                                    /**
                                     *  如果y轴滑动距离超过设置的处理范围，那么进行滑动事件处理,左侧改变亮度
                                     */
                                    if (mDownX < mScreenWidth * 0.5f) {
                                        mChangeBrightness = true;
                                        WindowManager.LayoutParams lp = Utils.getWindow(getContext()).getAttributes();
                                        if (lp.screenBrightness < 0) {
                                            try {
                                                mGestureDownBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                                                Log.i(TAG, "current system brightness: " + mGestureDownBrightness);
                                            } catch (Settings.SettingNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            mGestureDownBrightness = lp.screenBrightness * VideoType.MAX_BRIGHTNESS;
                                            Log.i(TAG, "current activity brightness: " + mGestureDownBrightness);
                                        }
                                    } else {//右侧改变声音
                                        mChangeVolume = true;
                                        mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    }
                                }
                            }
                        }
                    }
                    if (mChangePosition) {
                        long totalTimeDuration = getDuration();
                        mSeekTimePosition = (int) (mGestureDownPosition + deltaX * totalTimeDuration / mScreenWidth);
                        if (mSeekTimePosition > totalTimeDuration) {
                            mSeekTimePosition = totalTimeDuration;
                        }
                        String seekTime = Utils.stringForTime(mSeekTimePosition);
                        String totalTime = Utils.stringForTime(totalTimeDuration);

                        showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
                    }
                    if (mChangeVolume) {
                        deltaY = -deltaY;
                        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        int deltaV = (int) (max * deltaY * 3 / mScreenHeight);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
                        //dialog中显示百分比
                        int volumePercent = (int) (mGestureDownVolume * VideoType.MAX_PERCENT / max + deltaY * 3 * VideoType.MAX_PERCENT / mScreenHeight);
                        showVolumeDialog(-deltaY, volumePercent);
                    }

                    if (mChangeBrightness) {
                        deltaY = -deltaY;
                        int deltaV = (int) (VideoType.MAX_BRIGHTNESS * deltaY * 3 / mScreenHeight);
                        WindowManager.LayoutParams params = Utils.getWindow(getContext()).getAttributes();
                        /**
                         * 这和声音有区别，必须自己过滤一下负值
                         */
                        if (((mGestureDownBrightness + deltaV) / VideoType.MAX_BRIGHTNESS) >= 1) {
                            params.screenBrightness = 1;
                        } else if (((mGestureDownBrightness + deltaV) / VideoType.MAX_BRIGHTNESS) <= 0) {
                            params.screenBrightness = 0.01f;
                        } else {
                            params.screenBrightness = (mGestureDownBrightness + deltaV) / VideoType.MAX_BRIGHTNESS;
                        }
                        Utils.getWindow(getContext()).setAttributes(params);
                        //dialog中显示百分比
                        int brightnessPercent = (int) (mGestureDownBrightness * VideoType.MAX_PERCENT / VideoType.MAX_BRIGHTNESS + deltaY * 3 * VideoType.MAX_PERCENT / mScreenHeight);
                        showBrightnessDialog(brightnessPercent);
//                        mDownY = y;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "onTouch surfaceContainer actionUp [" + this.hashCode() + "] ");
                    mTouchingProgressBar = false;
                    dismissProgressDialog();
                    dismissVolumeDialog();
                    dismissBrightnessDialog();
                    if (mChangePosition) {
                        onEvent(UserAction.ON_TOUCH_SCREEN_SEEK_POSITION);
                        MediaManager.seekTo(mSeekTimePosition);
                        long duration = getDuration();
                        int progress = (int) (mSeekTimePosition * VideoType.MAX_PERCENT / (duration == 0 ? 1 : duration));
                        progressBar.setProgress(progress);
                    }
                    if (mChangeVolume) {
                        onEvent(UserAction.ON_TOUCH_SCREEN_SEEK_VOLUME);
                    }
                    startProgressTimer();
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    public void startVideo() {
        VideoPlayerManager.completeAll();
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ");
        initTextureView();
        addTextureView();
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        Utils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MediaManager.setDataSource(dataSourceObjects);
        MediaManager.setCurrentDataSource(Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
        MediaManager.instance().positionInList = positionInList;
        onStatePreparing();
        VideoPlayerManager.setFirstFloor(this);
    }

    public void onPrepared() {
        Log.i(TAG, "onPrepared " + " [" + this.hashCode() + "] ");
        onStatePrepared();
        onStatePlaying();
    }

    public void setState(int state) {
        setState(state, 0, 0);
    }

    public void setState(int state, int urlMapIndex, int seekToInAdvance) {
        switch (state) {
            case CURRENT_STATE_NORMAL:
                onStateNormal();
                break;
            case CURRENT_STATE_PREPARING:
                onStatePreparing();
                break;
            case CURRENT_STATE_PREPARING_CHANGING_URL:
                onStatePreparingChangingUrl(urlMapIndex, seekToInAdvance);
                break;
            case CURRENT_STATE_PLAYING:
                onStatePlaying();
                break;
            case CURRENT_STATE_PAUSE:
                onStatePause();
                break;
            case CURRENT_STATE_ERROR:
                onStateError();
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                onStateAutoComplete();
                break;
            default:
                break;
        }
    }

    public void onStateNormal() {
        Log.i(TAG, "onStateNormal " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_NORMAL;
        cancelProgressTimer();
    }

    public void onStatePreparing() {
        Log.i(TAG, "onStatePreparing " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_PREPARING;
        resetProgressAndTime();
    }

    public void onStatePreparingChangingUrl(int urlMapIndex, long seekToInAdvance) {
        currentState = CURRENT_STATE_PREPARING_CHANGING_URL;
        this.currentUrlMapIndex = urlMapIndex;
        this.seekToInAdvance = seekToInAdvance;
        MediaManager.setDataSource(dataSourceObjects);
        MediaManager.setCurrentDataSource(Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
        MediaManager.instance().prepare();
    }

    public void onStatePrepared() {//因为这个紧接着就会进入播放状态，所以不设置state
        if (seekToInAdvance != 0) {
            MediaManager.seekTo(seekToInAdvance);
            seekToInAdvance = 0;
        } else {
            long position = Utils.getSavedProgress(getContext(), Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
            if (position != 0) {
                MediaManager.seekTo(position);
            }
        }
    }

    public void onStatePlaying() {
        Log.i(TAG, "onStatePlaying " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_PLAYING;
        startProgressTimer();
    }

    public void onStatePause() {
        Log.i(TAG, "onStatePause " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_PAUSE;
        startProgressTimer();
    }

    public void onStateError() {
        Log.i(TAG, "onStateError " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_ERROR;
        cancelProgressTimer();
    }

    public void onStateAutoComplete() {
        Log.i(TAG, "onStateAutoComplete " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_AUTO_COMPLETE;
        cancelProgressTimer();
        progressBar.setProgress(VideoType.MAX_PERCENT);
        currentTimeTextView.setText(totalTimeTextView.getText());
    }

    public void onInfo(int what, int extra) {
        Log.d(TAG, "onInfo what - " + what + " extra - " + extra);
    }

    public void onError(int what, int extra) {
        Log.e(TAG, "onError " + what + " - " + extra + " [" + this.hashCode() + "] ");
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            onStateError();
            if (isCurrentPlay()) {
                MediaManager.instance().releaseMediaPlayer();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN || currentScreen == SCREEN_WINDOW_TINY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (widthRatio != 0 && heightRatio != 0) {
            int specWidth = MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = (int) ((specWidth * (float) heightRatio) / widthRatio);
            setMeasuredDimension(specWidth, specHeight);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY);
            getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public void onAutoCompletion() {
        Runtime.getRuntime().gc();
        Log.i(TAG, "onAutoCompletion " + " [" + this.hashCode() + "] ");
        onEvent(UserAction.ON_AUTO_COMPLETE);
        dismissVolumeDialog();
        dismissProgressDialog();
        dismissBrightnessDialog();
        cancelProgressTimer();
        onStateAutoComplete();

        if (currentScreen == SCREEN_WINDOW_FULLSCREEN || currentScreen == SCREEN_WINDOW_TINY) {
            backPress();
        }
        MediaManager.instance().releaseMediaPlayer();
        Utils.saveProgress(getContext(), Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), 0);
    }

    public void onCompletion() {
        Log.i(TAG, "onCompletion " + " [" + this.hashCode() + "] ");
        if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
            long position = getCurrentPositionWhenPlaying();
            Utils.saveProgress(getContext(), Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), position);
        }
        cancelProgressTimer();
        onStateNormal();
        textureViewContainer.removeView(MediaManager.textureView);
        MediaManager.instance().currentVideoWidth = 0;
        MediaManager.instance().currentVideoHeight = 0;

        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        Utils.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        clearFullscreenLayout();
        Utils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);
        if (MediaManager.surface != null) {
            MediaManager.surface.release();
        }

        if (MediaManager.savedSurfaceTexture != null) {
            MediaManager.savedSurfaceTexture.release();
        }

        MediaManager.textureView = null;
        MediaManager.savedSurfaceTexture = null;
    }

    public void release() {
        if (Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).equals(MediaManager.getCurrentDataSource()) &&
                (System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) > FULL_SCREEN_NORMAL_DELAY) {
            //在非全屏的情况下只能backPress()
            if (VideoPlayerManager.getSecondFloor() != null &&
                    VideoPlayerManager.getSecondFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN) {//点击全屏
            } else if (VideoPlayerManager.getSecondFloor() == null && VideoPlayerManager.getFirstFloor() != null &&
                    VideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN) {//直接全屏
            } else {
                Log.d(TAG, "releaseMediaPlayer [" + this.hashCode() + "]");
                releaseAllVideos();
            }
        }
    }

    public void initTextureView() {
        removeTextureView();
        MediaManager.textureView = new ResizeTextureView(getContext());
        MediaManager.textureView.setSurfaceTextureListener(MediaManager.instance());
    }

    public void addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureViewContainer.addView(MediaManager.textureView, layoutParams);
    }

    public void removeTextureView() {
        MediaManager.savedSurfaceTexture = null;
        if (MediaManager.textureView != null && MediaManager.textureView.getParent() != null) {
            ((ViewGroup) MediaManager.textureView.getParent()).removeView(MediaManager.textureView);
        }
    }

    public void clearFullscreenLayout() {
        ViewGroup vp = (Utils.scanForActivity(getContext()))
                .findViewById(Window.ID_ANDROID_CONTENT);
        View oldF = vp.findViewById(R.id.jz_fullscreen_id);
        View oldT = vp.findViewById(R.id.jz_tiny_id);
        if (oldF != null) {
            vp.removeView(oldF);
        }
        if (oldT != null) {
            vp.removeView(oldT);
        }
        showSupportActionBar(getContext());
    }

    public void clearFloatScreen() {
        Utils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);
        showSupportActionBar(getContext());
        VideoPlayer currJzvd = VideoPlayerManager.getCurrentHvd();
        currJzvd.textureViewContainer.removeView(MediaManager.textureView);
        ViewGroup vp = (Utils.scanForActivity(getContext()))
                .findViewById(Window.ID_ANDROID_CONTENT);
        vp.removeView(currJzvd);
        VideoPlayerManager.setSecondFloor(null);
    }

    public void onVideoSizeChanged() {
        Log.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ");
        if (MediaManager.textureView != null) {
            if (videoRotation != 0) {
                MediaManager.textureView.setRotation(videoRotation);
            }
            MediaManager.textureView.setVideoSize(MediaManager.instance().currentVideoWidth, MediaManager.instance().currentVideoHeight);
        }
    }

    public void startProgressTimer() {
        Log.i(TAG, "startProgressTimer: " + " [" + this.hashCode() + "] ");
        cancelProgressTimer();
        myHandler.sendEmptyMessageDelayed(WHAT_EXTRA, FULL_SCREEN_NORMAL_DELAY);
    }

    public void cancelProgressTimer() {
        if (myHandler != null) {
            myHandler.removeMessages(WHAT_EXTRA);
        }
    }

    public void setProgressAndText(int progress, long position, long duration) {
        if (!mTouchingProgressBar) {
            if (progress != 0) {
                progressBar.setProgress(progress);
            }
        }
        if (position != 0) {
            currentTimeTextView.setText(Utils.stringForTime(position));
        }
        totalTimeTextView.setText(Utils.stringForTime(duration));
    }

    public void setBufferProgress(int bufferProgress) {
        if (bufferProgress != 0) {
            progressBar.setSecondaryProgress(bufferProgress);
        }
    }

    public void resetProgressAndTime() {
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        currentTimeTextView.setText(Utils.stringForTime(0));
        totalTimeTextView.setText(Utils.stringForTime(0));
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (currentState == CURRENT_STATE_PLAYING ||
                currentState == CURRENT_STATE_PAUSE) {
            try {
                position = MediaManager.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }

    public long getDuration() {
        long duration = 0;
        //TODO MediaPlayer 判空的问题
//        if (MediaManager.instance().mediaPlayer == null) return duration;
        try {
            duration = MediaManager.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ");
        cancelProgressTimer();
        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ");
        onEvent(UserAction.ON_SEEK_POSITION);
        startProgressTimer();
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (currentState != CURRENT_STATE_PLAYING &&
                currentState != CURRENT_STATE_PAUSE) {
            return;
        }
        long time = seekBar.getProgress() * getDuration() / VideoType.MAX_PERCENT;
        MediaManager.seekTo(time);
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    public void startWindowFullscreen() {
        Log.i(TAG, "startWindowFullscreen " + " [" + this.hashCode() + "] ");
        hideSupportActionBar(getContext());
        Utils.setRequestedOrientation(getContext(), FULLSCREEN_ORIENTATION);

        ViewGroup vp = (Utils.scanForActivity(getContext()))
                .findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.jz_fullscreen_id);
        if (old != null) {
            vp.removeView(old);
        }
        textureViewContainer.removeView(MediaManager.textureView);
        try {
            Constructor<VideoPlayer> constructor = (Constructor<VideoPlayer>) VideoPlayer.this.getClass().getConstructor(Context.class);
            VideoPlayer videoPlayer = constructor.newInstance(getContext());
            videoPlayer.setId(R.id.jz_fullscreen_id);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(videoPlayer, lp);
            videoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            videoPlayer.setUp(dataSourceObjects, currentUrlMapIndex, VideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            videoPlayer.setState(currentState);
            videoPlayer.addTextureView();
            VideoPlayerManager.setSecondFloor(videoPlayer);
            onStateNormal();
            videoPlayer.progressBar.setSecondaryProgress(progressBar.getSecondaryProgress());
            videoPlayer.startProgressTimer();
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startWindowTiny() {
        Log.i(TAG, "startWindowTiny " + " [" + this.hashCode() + "] ");
        onEvent(UserAction.ON_ENTER_TINYSCREEN);
        if (currentState == CURRENT_STATE_NORMAL || currentState == CURRENT_STATE_ERROR
                || currentState == CURRENT_STATE_AUTO_COMPLETE) {
            return;
        }
        ViewGroup vp = (Utils.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.jz_tiny_id);
        if (old != null) {
            vp.removeView(old);
        }
        textureViewContainer.removeView(MediaManager.textureView);

        try {
            Constructor<VideoPlayer> constructor = (Constructor<VideoPlayer>) VideoPlayer.this.getClass().getConstructor(Context.class);
            VideoPlayer jzVideoPlayer = constructor.newInstance(getContext());
            jzVideoPlayer.setId(R.id.jz_tiny_id);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(400, 400);
            lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            vp.addView(jzVideoPlayer, lp);
            jzVideoPlayer.setUp(dataSourceObjects, currentUrlMapIndex, VideoPlayerStandard.SCREEN_WINDOW_TINY, objects);
            jzVideoPlayer.setState(currentState);
            jzVideoPlayer.addTextureView();
            VideoPlayerManager.setSecondFloor(jzVideoPlayer);
            onStateNormal();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCurrentPlay() {
        /**
         * 不仅正在播放的url不能一样，并且各个清晰度也不能一样
         */
        return isCurrentPlayer() && Utils.dataSourceObjectsContainsUri(dataSourceObjects,
                MediaManager.getCurrentDataSource());
    }

    public boolean isCurrentPlayer() {
        return VideoPlayerManager.getCurrentHvd() != null
                && VideoPlayerManager.getCurrentHvd() == this;
    }

    /**
     * 退出全屏和小窗的方法
     */
    public void playOnThisHvd() {
        Log.i(TAG, "playOnThisJzvd " + " [" + this.hashCode() + "] ");
        //1.清空全屏和小窗的Hvd
        currentState = VideoPlayerManager.getSecondFloor().currentState;
        currentUrlMapIndex = VideoPlayerManager.getSecondFloor().currentUrlMapIndex;
        clearFloatScreen();
        //2.在本Hvd上播放
        setState(currentState);
        addTextureView();
    }

    /**
     * 重力感应的时候调用的函数
     */
    public void autoFullscreen(float x) {
        if (isCurrentPlay()
                && currentState == CURRENT_STATE_PLAYING
                && currentScreen != SCREEN_WINDOW_FULLSCREEN
                && currentScreen != SCREEN_WINDOW_TINY) {
            if (x > 0) {
                Utils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                Utils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            onEvent(UserAction.ON_ENTER_FULLSCREEN);
            startWindowFullscreen();
        }
    }

    /**
     * 播放完成后自动退出全屏
     */
    public void autoQuitFullscreen() {
        if ((System.currentTimeMillis() - lastAutoFullscreenTime) > 2000
                && isCurrentPlay()
                && currentState == CURRENT_STATE_PLAYING
                && currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            lastAutoFullscreenTime = System.currentTimeMillis();
            backPress();
        }
    }

    public void onEvent(int type) {
        if (USER_EVENT != null && isCurrentPlay() && dataSourceObjects != null) {
            USER_EVENT.onEvent(type, Utils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), currentScreen, objects);
        }
    }

    public static void setMediaInterface(BaseMediaInterface mediaInterface) {
        MediaManager.instance().hMediaInterface = mediaInterface;
    }

    public void onSeekComplete() {

    }

    public void showWifiDialog(int event) {
    }

    public void showProgressDialog(float deltaX,
                                   String seekTime, long seekTimePosition,
                                   String totalTime, long totalTimeDuration) {
    }

    public void dismissProgressDialog() {

    }

    public void showVolumeDialog(float deltaY, int volumePercent) {

    }

    public void dismissVolumeDialog() {

    }

    public void showBrightnessDialog(int brightnessPercent) {

    }

    public void dismissBrightnessDialog() {

    }

    public static class AutoFullscreenListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {//可以得到传感器实时测量出来的变化值
            final float x = event.values[SensorManager.DATA_X];
            float y = event.values[SensorManager.DATA_Y];
            float z = event.values[SensorManager.DATA_Z];
            //过滤掉用力过猛会有一个反向的大数值
            boolean isResult = ((x > -15 && x < -10) || (x < 15 && x > 10)) && Math.abs(y) < 1.5;
            if (isResult) {
                if ((System.currentTimeMillis() - lastAutoFullscreenTime) > 2000) {
                    if (VideoPlayerManager.getCurrentHvd() != null) {
                        VideoPlayerManager.getCurrentHvd().autoFullscreen(x);
                    }
                    lastAutoFullscreenTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_EXTRA) {
                if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
                    long position = getCurrentPositionWhenPlaying();
                    long duration = getDuration();
                    int progress = (int) (position * VideoType.MAX_PERCENT / (duration == 0 ? 1 : duration));
                    setProgressAndText(progress, position, duration);
                    sendEmptyMessageDelayed(WHAT_EXTRA, FULL_SCREEN_NORMAL_DELAY);
                }
            }
        }
    }
}
