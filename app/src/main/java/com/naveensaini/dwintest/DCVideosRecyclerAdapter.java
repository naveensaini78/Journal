package com.naveensaini.dwintest;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naveensaini.dwintest.z_other_classes.VideoData;

import java.util.ArrayList;

public class DCVideosRecyclerAdapter extends RecyclerView.Adapter<DCVideosRecyclerAdapter.ViewHolder> {


    private Context context;
    private ArrayList<VideoData> videoDataList;
    private VideoView videoView;

    public DCVideosRecyclerAdapter(Context context, ArrayList<VideoData> videoDataList,
                                   VideoView videoView) {

        this.context = context;
        this.videoDataList = videoDataList;
        this.videoView = DAEntryViewActivity.dVideoPlayerView;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dc_videos_recycler_entry, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.i("XJDCV-onBindViewHolder", "Position - " + position);

        holder.videoThumbnail.setImageBitmap(videoDataList.get(position).videoBitmap);
        holder.videoPlayButton.setOnClickListener(v -> {
            Log.i("Nnsi-VideoPlay", "Video play button pressed");
            prepareVideoView(videoDataList.get(position).videoPath);

        });

    }


    private void prepareVideoView(String videoPath) {
        videoView.setVisibility(View.VISIBLE);

        Log.i("Nnsi-videoPath", videoPath);
        videoView.setVideoPath(videoPath);
        videoView.requestFocus();

        videoView.setOnPreparedListener(mp -> {
            Log.i("Nnsi-videoOnPrepared", "onPreparedListener");
            videoView.setLayoutParams(new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        });

        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();

        videoView.setOnCompletionListener(mp -> {
            videoView.setVisibility(View.GONE);
            // SJobs
        });

    }


    @Override
    public int getItemCount() {
        return videoDataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView videoThumbnail;
        protected ImageView videoPlayButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.dVideoThumbnail);
            videoPlayButton = itemView.findViewById(R.id.dRecyclerVideoPlayButton);
        }
    }
}
















