package com.example.bluesky.hmediaplayer.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.bluesky.hmediaplayer.MediaManager;
import com.example.bluesky.hmediaplayer.Utils;
import com.example.bluesky.hmediaplayer.VideoPlayer;
import com.example.bluesky.hmediaplayer.R;
import com.example.bluesky.hmediaplayer.modle.VideoConstant;
import com.example.bluesky.hmediaplayer.modle.VideoInfo;
import com.example.bluesky.hmediaplayer.view.adapter.AdapterRecyclerViewVideo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 037
 */
public class MoreVideoPlayerActivity extends AppCompatActivity implements AdapterRecyclerViewVideo.OnItemClick{
    RecyclerView recyclerView;
    AdapterRecyclerViewVideo adapterRecyclerViewVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        setContentView(R.layout.activity_more_video_player);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<VideoInfo> videoInfos = new ArrayList<>();
        for (int i= 0;i< VideoConstant.videoUrls.length;i++){
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setUrl(VideoConstant.videoUrlList[i]);
            videoInfo.setTitle(VideoConstant.videoTitle[i]);
            videoInfo.setUrl(VideoConstant.videoImg[i]);
        }
        adapterRecyclerViewVideo = new AdapterRecyclerViewVideo(this);
        adapterRecyclerViewVideo.setDataChange(videoInfos);
        adapterRecyclerViewVideo.setItemClick(this);
        recyclerView.setAdapter(adapterRecyclerViewVideo);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                VideoPlayer hVideoPlayer = view.findViewById(R.id.videoplayer);
                if (hVideoPlayer != null && Utils.dataSourceObjectsContainsUri(hVideoPlayer.dataSourceObjects, MediaManager.getCurrentDataSource())) {
                    VideoPlayer.releaseAllVideos();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (VideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoPlayer.releaseAllVideos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemTouch() {

    }
}
