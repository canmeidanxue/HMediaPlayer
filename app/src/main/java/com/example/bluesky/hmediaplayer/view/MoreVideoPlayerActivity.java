package com.example.bluesky.hmediaplayer.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.bluesky.hmediaplayer.HMediaManager;
import com.example.bluesky.hmediaplayer.HUtils;
import com.example.bluesky.hmediaplayer.HVideoPlayer;
import com.example.bluesky.hmediaplayer.R;
import com.example.bluesky.hmediaplayer.view.adapter.AdapterRecyclerViewVideo;

public class MoreVideoPlayerActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterRecyclerViewVideo adapterRecyclerViewVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setTitle("RecyclerView");
        setContentView(R.layout.activity_more_video_player);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
        adapterRecyclerViewVideo = new AdapterRecyclerViewVideo(this);
        recyclerView.setAdapter(adapterRecyclerViewVideo);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                HVideoPlayer hVideoPlayer = view.findViewById(R.id.videoplayer);
                if (hVideoPlayer != null && HUtils.dataSourceObjectsContainsUri(hVideoPlayer.dataSourceObjects, HMediaManager.getCurrentDataSource())) {
                    HVideoPlayer.releaseAllVideos();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (HVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        HVideoPlayer.releaseAllVideos();
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

}
