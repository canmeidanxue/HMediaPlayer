package com.example.bluesky.hmediaplayer.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.bluesky.hmediaplayer.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnSingleVideo).setOnClickListener(i->startActivity(new Intent(MainActivity.this,SigleVideoActivity.class)));
        findViewById(R.id.btnMoreVideo).setOnClickListener(i->startActivity(new Intent(MainActivity.this,MoreVideoPlayerActivity.class)));
    }
}
