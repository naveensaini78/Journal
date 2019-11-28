package com.naveensaini.dwintest;


import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naveensaini.dwintest.z_other_classes.AudioData;

import java.io.IOException;
import java.util.ArrayList;

public class DCAudioRecyclerAdapter extends RecyclerView.Adapter<DCAudioRecyclerAdapter.ViewHolder> {


    private Context context;
    private ArrayList<AudioData> audioDataList;
    private MediaPlayer mediaPlayer;


    public DCAudioRecyclerAdapter(Context context, ArrayList<AudioData> audioDataList) {
        this.context = context;
        this.audioDataList = audioDataList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dc_audio_recycler_entry, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String audioPath = audioDataList.get(position).audioPath;

        holder.nameText.setText(audioDataList.get(position).audioName);
        //holder.timerText.setText(SJobs)

        holder.playButton.setOnClickListener(v -> {
            Log.i("Nnsi-audioPlayer", audioPath);
            prepareMediaPlayer(audioPath, holder.playButton);

        });
        holder.stopButton.setOnClickListener(v -> {
            Toast.makeText(context, "Not Incorporated till now", Toast.LENGTH_SHORT).show();
        });


    }


    @Override
    public int getItemCount() {
        return audioDataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView playButton;
        protected ImageView stopButton;
        protected TextView nameText;
        protected TextView timerText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playButton = itemView.findViewById(R.id.dAudioRecyclerPlay);
            stopButton = itemView.findViewById(R.id.dAudioRecyclerStop);
            nameText = itemView.findViewById(R.id.dAudioRecyclerNameText);
            timerText = itemView.findViewById(R.id.dAudioRecyclerTimerText);
        }
    }


    ////////////////////////////////////////////////////////////
    public void prepareMediaPlayer(String audiopath, ImageView playButton) {

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audiopath);
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.i("MediaPlayer", "Ready");
                mp.start();
                playButton.setVisibility(View.GONE);

            });

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
                playButton.setVisibility(View.VISIBLE);
            });

            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
















