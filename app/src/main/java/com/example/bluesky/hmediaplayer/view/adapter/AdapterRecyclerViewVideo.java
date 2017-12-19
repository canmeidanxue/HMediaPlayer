package com.example.bluesky.hmediaplayer.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bluesky.hmediaplayer.VideoPlayer;
import com.example.bluesky.hmediaplayer.VideoPlayerStandard;
import com.example.bluesky.hmediaplayer.R;
import com.example.bluesky.hmediaplayer.modle.VideoConstant;
import com.squareup.picasso.Picasso;


public class AdapterRecyclerViewVideo extends RecyclerView.Adapter<AdapterRecyclerViewVideo.MyViewHolder> {

    public static final String TAG = "AdapterRecyclerViewVideo";
    int[] videoIndexs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private Context context;

    public AdapterRecyclerViewVideo(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_videoview, parent,
                false));
        return holder;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder [" + holder.jzVideoPlayer.hashCode() + "] position=" + position);

        holder.jzVideoPlayer.setUp(
                VideoConstant.videoUrls[0][position], VideoPlayer.SCREEN_WINDOW_LIST,
                VideoConstant.videoTitles[0][position]);
        Picasso.with(holder.jzVideoPlayer.getContext())
                .load(VideoConstant.videoThumbs[0][position])
                .into(holder.jzVideoPlayer.thumbImageView);
    }

    @Override
    public int getItemCount() {
        return videoIndexs.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        VideoPlayerStandard jzVideoPlayer;

        public MyViewHolder(View itemView) {
            super(itemView);
            jzVideoPlayer = itemView.findViewById(R.id.videoplayer);
        }
    }

}
